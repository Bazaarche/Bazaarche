package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.entity.TransactionBuilder
import com.phelat.poolakey.config.PaymentConfiguration
import com.phelat.poolakey.config.SecurityCheck
import com.phelat.poolakey.entity.PurchaseEntity
import com.phelat.poolakey.exception.BazaarNotFoundException
import com.phelat.poolakey.request.PurchaseRequest
import com.phelat.poolakey.rx.exception.PurchaseCanceledException
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

internal class BazaarIabViewModel(private val transaction: TransactionBuilder,
                                  private val bazaarIabInteract: BazaarIabUseCases,
                                  private val scheduler: Scheduler) : ViewModel() {

  private val disposables = CompositeDisposable()

  private val _purchaseState = MutableLiveData<PurchaseState>().apply {
    value = PurchaseState.InProgress
  }
  internal val purchaseState: LiveData<PurchaseState> = _purchaseState

  internal val paymentConfiguration = run {
    val securityCheck = SecurityCheck.Enable(rsaPublicKey = BuildConfig.BAZAARCHE_IAB_KEY)
    PaymentConfiguration(localSecurityCheck = securityCheck)
  }

  var animationDuration: Long = 0

  internal fun onConnectionError(throwable: Throwable) {
    if (throwable is BazaarNotFoundException) {
      _purchaseState.value = PurchaseState.BazaarNotFoundError
    } else {
      throw throwable
    }
  }

  internal fun getPurchaseRequest(): LiveData<PurchaseRequest> {

    val purchaseRequestLiveData = MutableLiveData<PurchaseRequest>()

    disposables.add(bazaarIabInteract.getPurchaseRequest()
        .subscribeOn(scheduler)
        .subscribe(Consumer {
          purchaseRequestLiveData.postValue(it)
        })
    )

    return purchaseRequestLiveData
  }

  internal fun onPurchaseFinished(data: Intent?, purchaseResult: Single<PurchaseEntity>) {

    disposables.add(purchaseResult
        .flatMap {
          bazaarIabInteract.getPurchaseInfo(data!!, it)
        }
        .flatMap { bazaarIabInteract.getPurchaseBundle(it.uid) }
        .map { PurchaseState.Finished(it) }
        .flatMapObservable { purchaseState: PurchaseState ->
          Observable.just(purchaseState)
              .delay(animationDuration, TimeUnit.MILLISECONDS)
              .observeOn(AndroidSchedulers.mainThread())
              .startWith(PurchaseState.Purchased)
        }
        .doOnError(Throwable::printStackTrace)
        .onErrorReturn(::mapError)
        .subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(::onSuccess))
  }

  private fun onSuccess(purchaseState: PurchaseState) {
    _purchaseState.value = purchaseState
  }

  override fun onCleared() {
    disposables.clear()
  }

  fun onCancelInstallation() {
    _purchaseState.value = PurchaseState.Canceled(bazaarIabInteract.getCancelBundle())
  }

  private fun mapError(throwable: Throwable): PurchaseState {
    return when (throwable) {
      is PurchaseCanceledException -> PurchaseState.Canceled(bazaarIabInteract.getCancelBundle())

      is UnknownHostException, is ConnectException, is SocketTimeoutException -> {
        PurchaseState.NetworkError(bazaarIabInteract.getErrorBundle())
      }
      else -> PurchaseState.Error(bazaarIabInteract.getErrorBundle())
    }
  }

}