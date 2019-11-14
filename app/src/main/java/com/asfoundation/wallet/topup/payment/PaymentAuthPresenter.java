package com.asfoundation.wallet.topup.payment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.utils.AmountUtil;
import com.appcoins.wallet.billing.BillingMessagesMapper;
import com.asfoundation.wallet.billing.BillingService;
import com.asfoundation.wallet.billing.adyen.Adyen;
import com.asfoundation.wallet.billing.adyen.PaymentType;
import com.asfoundation.wallet.billing.authorization.AdyenAuthorization;
import com.asfoundation.wallet.topup.CurrencyData;
import com.asfoundation.wallet.topup.TopUpData;
import com.asfoundation.wallet.ui.iab.FiatValue;
import com.asfoundation.wallet.ui.iab.InAppPurchaseInteractor;
import com.asfoundation.wallet.ui.iab.Navigator;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentAuthPresenter {

  private static final String WAITING_RESULT = "WAITING_RESULT";

  private final Scheduler viewScheduler;
  private final Scheduler networkScheduler;
  private final CompositeDisposable disposables;
  private final Adyen adyen;
  private final BillingService billingService;
  private final Navigator navigator;
  private final BillingMessagesMapper billingMessagesMapper;
  private final InAppPurchaseInteractor inAppPurchaseInteractor;
  private final String bonusValue;
  private final String appPackage;
  private PaymentAuthView view;
  private boolean waitingResult;

  public PaymentAuthPresenter(PaymentAuthView view, String appPackage, Scheduler viewScheduler,
      Scheduler networkScheduler, CompositeDisposable disposables, Adyen adyen,
      BillingService billingService, Navigator navigator,
      BillingMessagesMapper billingMessagesMapper, InAppPurchaseInteractor inAppPurchaseInteractor,
      String bonusValue) {
    this.view = view;
    this.appPackage = appPackage;
    this.viewScheduler = viewScheduler;
    this.networkScheduler = networkScheduler;
    this.disposables = disposables;
    this.adyen = adyen;
    this.billingService = billingService;
    this.navigator = navigator;
    this.billingMessagesMapper = billingMessagesMapper;
    this.inAppPurchaseInteractor = inAppPurchaseInteractor;
    this.bonusValue = bonusValue;
  }

  public void present(@Nullable Bundle savedInstanceState, String transactionOrigin,
      CurrencyData currencyData, String selectedCurrency, String currency, String transactionType,
      PaymentType paymentType) {
    adyen.createNewPayment();

    if (savedInstanceState != null) {
      waitingResult = savedInstanceState.getBoolean(WAITING_RESULT);
    }

    onViewCreatedCompletePayment(transactionOrigin, currencyData, selectedCurrency, currency,
        transactionType);

    onViewCreatedSelectPaymentMethod(paymentType);

    onViewCreatedShowPaymentMethodInputView();

    onViewCreatedCheckAuthorizationActive(transactionOrigin, currencyData, selectedCurrency,
        currency, transactionType);

    onViewCreatedCheckAuthorizationFailed(transactionOrigin, currencyData, selectedCurrency,
        currency, transactionType);

    onViewCreatedCheckAuthorizationProcessing(transactionOrigin, currencyData, selectedCurrency,
        currency, transactionType);

    handlePaymentMethodResults();

    handleChangeCardMethodResults();

    handleAdyenUriRedirect();

    handleAdyenUriResult();

    handleErrorDismissEvent();

    handleAdyenPaymentResult();

    handleFieldValidationStateChange();
  }

  private void onViewCreatedShowPaymentMethodInputView() {
    disposables.add(adyen.getPaymentRequest()
        .filter(paymentRequest -> paymentRequest.getPaymentMethod() != null)
        .map(paymentRequest -> paymentRequest.getPaymentMethod()
            .getType())
        .distinctUntilChanged(
            (paymentRequest, paymentRequest2) -> paymentRequest.equals(paymentRequest2))
        .flatMapMaybe(type -> adyen.getPaymentRequest()
            .firstElement())
        .observeOn(viewScheduler)
        .doOnNext(data -> {
          if (data.getPaymentMethod()
              .getType()
              .equals(PaymentMethod.Type.CARD)) {
            view.showCreditCardView(data.getPaymentMethod(),
                AmountUtil.format(data.getAmount(), false), data.getAmount()
                    .getCurrency(), true, data.getShopperReference() != null, data.getPublicKey(),
                data.getGenerationTime());
          } else {
            view.showCvcView(data.getPaymentMethod(), AmountUtil.format(data.getAmount(), false),
                data.getAmount()
                    .getCurrency());
          }
        })
        .observeOn(viewScheduler)
        .subscribe(__ -> {
        }, this::showError));
  }

  private void showError(Throwable throwable) {
    throwable.printStackTrace();

    if (throwable instanceof IOException) {
      view.hideLoading();
      view.showNetworkError();
    } else {
      view.showGenericError();
    }
  }

  private void onViewCreatedCompletePayment(String transactionOrigin, CurrencyData currencyData,
      String selectedCurrency, String currency, String transactionType) {
    disposables.add(Completable.fromAction(() -> view.showLoading())
        .observeOn(networkScheduler)
        .andThen(convertAmount(currencyData, selectedCurrency).flatMapCompletable(
            value -> billingService.getAuthorization(transactionOrigin, value, currency,
                transactionType, appPackage, null)
                .observeOn(viewScheduler)
                .filter(AdyenAuthorization::isPendingAuthorization)
                .firstOrError()
                .flatMapCompletable(
                    authorization -> adyen.completePayment(authorization.getSession()))))
        .observeOn(viewScheduler)
        .subscribe(() -> {
        }, this::showError));
  }

  @NonNull
  private Single<BigDecimal> convertAmount(CurrencyData currencyData, String selectedCurrency) {
    if (selectedCurrency.equals(TopUpData.FIAT_CURRENCY)) {
      return Single.just(new BigDecimal(currencyData.getFiatValue()));
    }
    return inAppPurchaseInteractor.convertToLocalFiat(
        (new BigDecimal(currencyData.getAppcValue())).doubleValue())
        .map(FiatValue::getAmount);
  }

  private void onViewCreatedSelectPaymentMethod(PaymentType paymentType) {
    disposables.add(adyen.getPaymentMethod(paymentType)
        .flatMapCompletable(adyen::selectPaymentService)
        .observeOn(viewScheduler)
        .subscribe(() -> {
        }, this::showError));
  }

  private void onViewCreatedCheckAuthorizationActive(String transactionOrigin,
      CurrencyData currencyData, String selectedCurrency, String currency, String transactionType) {
    disposables.add(convertAmount(currencyData, selectedCurrency).flatMap(
        value -> billingService.getAuthorization(transactionOrigin, value, currency,
            transactionType, appPackage, null)
            .filter(AdyenAuthorization::isCompleted)
            .firstOrError()
            .flatMap(adyenAuthorization -> createBundle())
            .observeOn(viewScheduler)
            .doOnSuccess(bundle -> {
              waitingResult = false;
              navigator.popView(bundle);
            }))
        .subscribeOn(networkScheduler)
        .observeOn(viewScheduler)
        .subscribe(__ -> {
        }, this::showError));
  }

  private Single<Bundle> createBundle() {
    return inAppPurchaseInteractor.getTransactionAmount(billingService.getTransactionUid())
        .retryWhen(errors -> {
          AtomicInteger counter = new AtomicInteger();
          return errors.takeWhile(e -> counter.getAndIncrement() != 3)
              .flatMap(e -> Flowable.timer(counter.get(), TimeUnit.SECONDS));
        })
        .map(price -> billingMessagesMapper.topUpBundle(price.getValue(), price.getCurrency(),
            bonusValue));
  }

  private void onViewCreatedCheckAuthorizationFailed(String transactionOrigin,
      CurrencyData currencyData, String selectedCurrency, String currency, String transactionType) {
    disposables.add(convertAmount(currencyData, selectedCurrency).flatMap(
        value -> billingService.getAuthorization(transactionOrigin, value, currency,
            transactionType, appPackage, null)
            .filter(AdyenAuthorization::isFailed)
            .firstOrError()
            .observeOn(viewScheduler)
            .doOnSuccess(this::showError))
        .subscribeOn(networkScheduler)
        .observeOn(viewScheduler)
        .subscribe(__ -> {
        }, this::showError));
  }

  private void showError(AdyenAuthorization adyenAuthorization) {
    view.showPaymentRefusedError(adyenAuthorization);
  }

  private void onViewCreatedCheckAuthorizationProcessing(String transactionOrigin,
      CurrencyData currencyData, String selectedCurrency, String currency, String transactionType) {
    disposables.add(convertAmount(currencyData, selectedCurrency).map(
        value -> billingService.getAuthorization(transactionOrigin, value, currency,
            transactionType, appPackage, null)
            .filter(AdyenAuthorization::isProcessing)
            .observeOn(viewScheduler)
            .doOnNext(__ -> view.showLoading()))
        .subscribeOn(networkScheduler)
        .observeOn(viewScheduler)
        .subscribe(__ -> {
        }, this::showError));
  }

  private void handlePaymentMethodResults() {
    disposables.add(view.paymentMethodDetailsEvent()
        .doOnNext(__ -> view.showFinishingLoading())
        .flatMapCompletable(adyen::finishPayment)
        .observeOn(viewScheduler)
        .subscribe(() -> {
        }, this::showError));
  }

  private void handleChangeCardMethodResults() {
    disposables.add(view.changeCardMethodDetailsEvent()
        .doOnNext(__ -> view.showLoading())
        .flatMapCompletable(paymentMethod -> adyen.deletePaymentMethod())
        .observeOn(viewScheduler)
        .subscribe(() -> {
        }, this::showError));
  }

  private void handleAdyenUriResult() {
    disposables.add(navigator.uriResults()
        .flatMapCompletable(adyen::finishUri)
        .observeOn(viewScheduler)
        .subscribe(() -> {
        }, this::showError));
  }

  private void handleAdyenUriRedirect() {
    disposables.add(adyen.getRedirectUrl()
        .observeOn(viewScheduler)
        .filter(s -> !waitingResult)
        .doOnSuccess(redirectUrl -> {
          view.showLoading();
          navigator.navigateToUriForResult(redirectUrl);
          waitingResult = true;
        })
        .subscribe(__ -> {
        }, this::showError));
  }

  private void handleErrorDismissEvent() {
    disposables.add(
        Observable.merge(view.errorDismisses(), view.errorCancels(), view.errorPositiveClicks())
            .subscribe(__ -> navigator.popViewWithError(), Throwable::printStackTrace));
  }

  private void handleAdyenPaymentResult() {
    disposables.add(adyen.getPaymentResult()
        .flatMapCompletable(result -> {
          if (result.isProcessed()) {
            if (result.getPayment() != null
                && result.getPayment()
                .getPaymentStatus() == Payment.PaymentStatus.CANCELLED) {
              view.cancelPayment();
              return Completable.complete();
            }
            view.setFinishingPurchase();
            return billingService.authorize(result.getPayment(), result.getPayment()
                .getPayload());
          }
          return Completable.error(result.getError());
        })
        .observeOn(viewScheduler)
        .subscribe(() -> {
        }, this::showError));
  }

  private void handleFieldValidationStateChange() {
    disposables.add(view.onValidFieldStateChange()
        .observeOn(viewScheduler)
        .doOnNext(valid -> view.updateTopUpButton(valid))
        .subscribe());
  }

  public void stop() {
    disposables.clear();
  }

  void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(WAITING_RESULT, waitingResult);
  }
}