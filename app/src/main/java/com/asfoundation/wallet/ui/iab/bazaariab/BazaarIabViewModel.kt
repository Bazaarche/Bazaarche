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
import com.phelat.poolakey.request.PurchaseRequest
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

internal class BazaarIabViewModel(private val transaction: TransactionBuilder,
                                  private val bazaarIabInteract: BazaarIabInteract,
                                  private val scheduler: Scheduler) : ViewModel() {

  private val disposables = CompositeDisposable()

  internal val paymentConfiguration = run {
    val securityCheck = SecurityCheck.Enable(rsaPublicKey = BuildConfig.BAZAARCHE_IAB_KEY)
    PaymentConfiguration(localSecurityCheck = securityCheck)
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


  fun onPurchaseFinished(data: Intent, purchaseEntity: PurchaseEntity) {

    disposables.add(bazaarIabInteract.getPurchaseInfo(data, purchaseEntity)
        .flatMapCompletable {
          bazaarIabInteract.waitTransactionCompletion(it.uid)
        }
        .andThen(bazaarIabInteract.getPurchaseBundle(transaction.domain, transaction.skuId))
        .subscribeOn(scheduler)
        .subscribe(Consumer {
        }))
  }

  override fun onCleared() {
    disposables.clear()
  }
}