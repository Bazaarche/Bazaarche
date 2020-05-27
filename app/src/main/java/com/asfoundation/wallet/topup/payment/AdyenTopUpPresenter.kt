package com.asfoundation.wallet.topup.payment

import android.os.Bundle
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.appcoins.wallet.billing.BillingMessagesMapper
import com.appcoins.wallet.billing.adyen.AdyenPaymentRepository
import com.appcoins.wallet.billing.adyen.PaymentModel
import com.appcoins.wallet.billing.adyen.TransactionResponse.Status
import com.appcoins.wallet.billing.adyen.TransactionResponse.Status.CANCELED
import com.asf.wallet.R
import com.asfoundation.wallet.billing.adyen.AdyenErrorCodeMapper
import com.asfoundation.wallet.billing.adyen.AdyenErrorCodeMapper.Companion.CVC_DECLINED
import com.asfoundation.wallet.billing.adyen.AdyenPaymentInteractor
import com.asfoundation.wallet.billing.adyen.PaymentType
import com.asfoundation.wallet.topup.CurrencyData
import com.asfoundation.wallet.topup.TopUpAnalytics
import com.asfoundation.wallet.topup.TopUpData
import com.asfoundation.wallet.ui.iab.FiatValue
import com.asfoundation.wallet.ui.iab.Navigator
import com.asfoundation.wallet.util.CurrencyFormatUtils
import com.asfoundation.wallet.util.WalletCurrency
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class AdyenTopUpPresenter(private val view: AdyenTopUpView,
                          private val appPackage: String,
                          private val viewScheduler: Scheduler,
                          private val networkScheduler: Scheduler,
                          private val disposables: CompositeDisposable,
                          private val returnUrl: String,
                          private val paymentType: String,
                          private val transactionType: String,
                          private val amount: String,
                          private val currency: String,
                          private val currencyData: CurrencyData,
                          private val selectedCurrency: String,
                          private val navigator: Navigator,
                          private val billingMessagesMapper: BillingMessagesMapper,
                          private val adyenPaymentInteractor: AdyenPaymentInteractor,
                          private val bonusValue: String,
                          private val adyenErrorCodeMapper: AdyenErrorCodeMapper,
                          private val gamificationLevel: Int,
                          private val topUpAnalytics: TopUpAnalytics,
                          private val formatter: CurrencyFormatUtils
) {

  private var waitingResult = false
  private var paymentMethod = PaymentType.CARD.name

  fun present(savedInstanceState: Bundle?) {
    if (savedInstanceState != null) {
      waitingResult = savedInstanceState.getBoolean(WAITING_RESULT)
    }
    loadPaymentMethodInfo(savedInstanceState)
    handleForgetCardClick()

    handleRetryClick(savedInstanceState)
    handleRedirectResponse()
    handleSupportClicks()
    handleTryAgainClicks()
  }

  private fun handleRetryClick(savedInstanceState: Bundle?) {
    disposables.add(view.retryClick()
        .observeOn(viewScheduler)
        .doOnNext { view.showRetryAnimation() }
        .delay(1, TimeUnit.SECONDS)
        .doOnNext {
          if (waitingResult) {
            view.navigateToPaymentSelection()
          } else {
            loadPaymentMethodInfo(savedInstanceState)
          }
        }
        .subscribe({}, { it.printStackTrace() }))
  }

  private fun handleSupportClicks() {
    disposables.add(
        view.getSupportClicks()
            .throttleFirst(50, TimeUnit.MILLISECONDS)
            .flatMapCompletable { adyenPaymentInteractor.showSupport(gamificationLevel) }
            .subscribeOn(viewScheduler)
            .subscribe()
    )
  }

  private fun handleTryAgainClicks() {
    disposables.add(
        view.getTryAgainClicks()
            .throttleFirst(50, TimeUnit.MILLISECONDS)
            .doOnNext {
              if (paymentMethod == PaymentType.CARD.name) view.hideSpecificError()
              else view.navigateToPaymentSelection()
            }
            .subscribeOn(viewScheduler)
            .subscribe()
    )
  }

  private fun loadPaymentMethodInfo(savedInstanceState: Bundle?) {
    disposables.add(convertAmount()
        .flatMap {
          adyenPaymentInteractor.loadPaymentInfo(mapPaymentToService(paymentType), it.toString(),
              currency)
        }
        .subscribeOn(networkScheduler)
        .observeOn(viewScheduler)
        .doOnSuccess {
          view.hideNoNetworkError()
          view.hideLoading()
          if (it.error.hasError) {
            if (it.error.isNetworkError) view.showNetworkError()
            else view.showSpecificError(R.string.unknown_error)
          } else {
            val priceAmount = formatter.formatCurrency(it.priceAmount, WalletCurrency.FIAT)
            view.showValues(priceAmount, it.priceCurrency)
            paymentMethod = paymentType
            if (paymentType == PaymentType.CARD.name) {
              view.finishCardConfiguration(it.paymentMethodInfo!!, it.isStored, false,
                  savedInstanceState)
              handleTopUpClick(it.priceAmount, it.priceCurrency, currencyData.appcValue)
            } else if (paymentType == PaymentType.PAYPAL.name) {
              launchPaypal(it.paymentMethodInfo!!, it.priceAmount, it.priceCurrency)
            }
          }
        }
        .subscribe({}, { it.printStackTrace() }))
  }

  private fun launchPaypal(paymentMethodInfo: PaymentMethod, priceAmount: BigDecimal,
                           priceCurrency: String) {
    disposables.add(
        adyenPaymentInteractor.makeTopUpPayment(paymentMethodInfo, false,
            returnUrl, priceAmount.toString(), priceCurrency,
            mapPaymentToService(paymentType).transactionType,
            transactionType, appPackage)
            .subscribeOn(networkScheduler)
            .observeOn(viewScheduler)
            .filter { !waitingResult }
            .doOnSuccess { handlePaymentModel(it, priceAmount, priceCurrency) }
            .subscribe())
  }

  //Called if is card
  private fun handleTopUpClick(priceAmount: BigDecimal, priceCurrency: String, appcValue: String) {
    disposables.add(
        view.topUpButtonClicked()
            .flatMapSingle {
              view.retrievePaymentData()
                  .firstOrError()
            }
            .doOnNext {
              view.showLoading()
              view.lockRotation()
              view.setFinishingPurchase()
            }
            .observeOn(networkScheduler)
            .flatMapSingle {
              topUpAnalytics.sendConfirmationEvent(appcValue.toDouble(), "top_up",
                  paymentType)
              adyenPaymentInteractor.makeTopUpPayment(it.cardPaymentMethod, it.shouldStoreCard,
                  returnUrl, priceAmount.toString(), priceCurrency,
                  mapPaymentToService(paymentType).transactionType, transactionType, appPackage)
            }
            .observeOn(viewScheduler)
            .flatMapCompletable {
              handlePaymentResult(it, priceAmount, priceCurrency, currencyData.appcValue)
            }
            .subscribe({}, { it.printStackTrace() }))
  }

  private fun handleForgetCardClick() {
    disposables.add(view.forgetCardClick()
        .observeOn(viewScheduler)
        .doOnNext { view.showLoading() }
        .observeOn(networkScheduler)
        .flatMapSingle { adyenPaymentInteractor.disablePayments() }
        .observeOn(viewScheduler)
        .doOnNext { success -> if (!success) view.showSpecificError(R.string.unknown_error) }
        .filter { it }
        .observeOn(networkScheduler)
        .flatMapSingle {
          adyenPaymentInteractor.loadPaymentInfo(mapPaymentToService(paymentType),
              amount, currency)
              .observeOn(viewScheduler)
              .doOnSuccess {
                view.hideLoading()
                if (it.error.hasError) {
                  if (it.error.isNetworkError) view.showNetworkError()
                  else view.showSpecificError(R.string.unknown_error)
                } else {
                  view.finishCardConfiguration(it.paymentMethodInfo!!, it.isStored, true, null)
                }
              }
        }
        .subscribe())
  }

  private fun handleRedirectResponse() {
    disposables.add(navigator.uriResults()
        .doOnNext {
          topUpAnalytics.sendPaypalUrlEvent(currencyData.appcValue.toDouble(), paymentType,
              it.getQueryParameter("type"), it.getQueryParameter("resultCode"), it.toString())
        }
        .observeOn(viewScheduler)
        .doOnNext { view.submitUriResult(it) }
        .subscribe())
  }

  //Called if is paypal
  private fun handlePaymentDetails(priceAmount: BigDecimal,
                                   priceCurrency: String) {
    disposables.add(view.getPaymentDetails()
        .observeOn(viewScheduler)
        .doOnNext {
          view.hideKeyboard()
          view.setFinishingPurchase()
        }
        .observeOn(networkScheduler)
        .flatMapSingle { adyenPaymentInteractor.submitRedirect(it.uid, it.details, it.paymentData) }
        .observeOn(viewScheduler)
        .flatMapCompletable {
          handlePaymentResult(it, priceAmount, priceCurrency, currencyData.appcValue)
        }
        .subscribe())
  }

  private fun handlePaymentResult(paymentModel: PaymentModel, priceAmount: BigDecimal,
                                  priceCurrency: String, appcValue: String): Completable {
    return when {
      paymentModel.resultCode.equals("AUTHORISED", ignoreCase = true) -> {
        adyenPaymentInteractor.getTransaction(paymentModel.uid)
            .subscribeOn(networkScheduler)
            .observeOn(viewScheduler)
            .flatMapCompletable {
              if (it.status == Status.COMPLETED) {
                Completable.fromAction {
                  topUpAnalytics.sendSuccessEvent(appcValue.toDouble(), paymentType,
                      "success")
                  val bundle =
                      createBundle(priceAmount, priceCurrency, currencyData.fiatCurrencySymbol)
                  waitingResult = false
                  navigator.popView(bundle)
                }
              } else {
                Completable.fromAction {
                  topUpAnalytics.sendErrorEvent(appcValue.toDouble(), paymentType, "error",
                      it.error.code.toString(),
                      buildRefusalReason(it.status, it.error.message))
                  view.showSpecificError(R.string.unknown_error)
                }
              }
            }
      }
      paymentModel.refusalReason != null -> Completable.fromAction {
        topUpAnalytics.sendErrorEvent(appcValue.toDouble(), paymentType, "error",
            paymentModel.refusalCode.toString(), paymentModel.refusalReason ?: "")
        paymentModel.refusalCode?.let { code ->
          if (code == CVC_DECLINED) {
            view.showCvvError()
          } else {
            view.showSpecificError(adyenErrorCodeMapper.map(code))
          }
        }
      }
      paymentModel.error.hasError -> Completable.fromAction {
        if (paymentModel.error.isNetworkError) {
          topUpAnalytics.sendErrorEvent(priceAmount.toDouble(), paymentType, "error",
              paymentModel.error.code.toString(),
              "network_error")
          view.showNetworkError()
        } else {
          topUpAnalytics.sendErrorEvent(appcValue.toDouble(), paymentType, "error",
              paymentModel.error.code.toString(), paymentModel.error.message ?: "")
          view.showSpecificError(R.string.unknown_error)
        }
      }
      paymentModel.status == CANCELED -> Completable.fromAction {
        topUpAnalytics.sendErrorEvent(appcValue.toDouble(), paymentType, "error", "",
            "canceled")
        view.cancelPayment()
      }
      else -> Completable.fromAction {
        topUpAnalytics.sendErrorEvent(appcValue.toDouble(), paymentType, "error",
            paymentModel.refusalCode.toString(), "Generic Error")
        view.showSpecificError(R.string.unknown_error)
      }
    }
  }

  private fun buildRefusalReason(status: Status, message: String?): String {
    return message?.let { "$status : $it" } ?: status.toString()
  }

  private fun handlePaymentModel(paymentModel: PaymentModel,
                                 priceAmount: BigDecimal, priceCurrency: String) {
    if (paymentModel.error.hasError) {
      if (paymentModel.error.isNetworkError) view.showNetworkError()
      else view.showSpecificError(R.string.unknown_error)
    } else {
      view.showLoading()
      view.lockRotation()
      view.setRedirectComponent(paymentModel.uid, paymentModel.action!!)
      handlePaymentDetails(priceAmount, priceCurrency)
      navigator.navigateToUriForResult(paymentModel.redirectUrl)
      waitingResult = true
    }
  }

  private fun convertAmount(): Single<BigDecimal> {
    return if (selectedCurrency == TopUpData.FIAT_CURRENCY) {
      Single.just(BigDecimal(currencyData.fiatValue))
    } else adyenPaymentInteractor.convertToLocalFiat(
        BigDecimal(currencyData.appcValue).toDouble())
        .map(FiatValue::amount)
  }

  private fun createBundle(priceAmount: BigDecimal, priceCurrency: String,
                           fiatCurrencySymbol: String): Bundle {
    return billingMessagesMapper.topUpBundle(priceAmount.toPlainString(), priceCurrency, bonusValue,
        fiatCurrencySymbol)
  }

  private fun mapPaymentToService(paymentType: String): AdyenPaymentRepository.Methods {
    return if (paymentType == PaymentType.CARD.name) {
      AdyenPaymentRepository.Methods.CREDIT_CARD
    } else {
      AdyenPaymentRepository.Methods.PAYPAL
    }
  }

  fun stop() {
    disposables.clear()
  }

  fun onSaveInstanceState(outState: Bundle) {
    outState.putBoolean(WAITING_RESULT, waitingResult)
  }

  companion object {
    private const val WAITING_RESULT = "WAITING_RESULT"
  }

}