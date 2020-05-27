package com.asfoundation.wallet.ui.iab;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.appcoins.wallet.appcoins.rewards.AppcoinsRewards;
import com.appcoins.wallet.bdsbilling.Billing;
import com.appcoins.wallet.bdsbilling.mappers.ExternalBillingSerializer;
import com.appcoins.wallet.bdsbilling.repository.entity.Gateway;
import com.appcoins.wallet.bdsbilling.repository.entity.PaymentMethodEntity;
import com.appcoins.wallet.bdsbilling.repository.entity.Price;
import com.appcoins.wallet.bdsbilling.repository.entity.Purchase;
import com.appcoins.wallet.bdsbilling.repository.entity.Transaction;
import com.appcoins.wallet.bdsbilling.repository.entity.Transaction.Status;
import com.appcoins.wallet.billing.BillingMessagesMapper;
import com.appcoins.wallet.billing.repository.entity.TransactionData;
import com.asf.wallet.BuildConfig;
import com.asfoundation.wallet.entity.TransactionBuilder;
import com.asfoundation.wallet.util.BalanceUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InAppPurchaseInteractor {

  public static final String PRE_SELECTED_PAYMENT_METHOD_KEY = "PRE_SELECTED_PAYMENT_METHOD_KEY";
  private static final String LOCAL_PAYMENT_METHOD_KEY = "LOCAL_PAYMENT_METHOD_KEY";
  private static final String LAST_USED_PAYMENT_METHOD_KEY = "LAST_USED_PAYMENT_METHOD_KEY";
  private static final String APPC_ID = "appcoins";
  private static final String CREDITS_ID = "appcoins_credits";
  private static final long EARN_APPCOINS_APTOIDE_VERCODE = 9961;
  private final AsfInAppPurchaseInteractor asfInAppPurchaseInteractor;
  private final BdsInAppPurchaseInteractor bdsInAppPurchaseInteractor;
  private final ExternalBillingSerializer billingSerializer;
  private final AppcoinsRewards appcoinsRewards;
  private final Billing billing;
  private final SharedPreferences sharedPreferences;
  private final PackageManager packageManager;

  public InAppPurchaseInteractor(AsfInAppPurchaseInteractor asfInAppPurchaseInteractor,
      BdsInAppPurchaseInteractor bdsInAppPurchaseInteractor,
      ExternalBillingSerializer billingSerializer, AppcoinsRewards appcoinsRewards, Billing billing,
      SharedPreferences sharedPreferences, PackageManager packageManager) {
    this.asfInAppPurchaseInteractor = asfInAppPurchaseInteractor;
    this.bdsInAppPurchaseInteractor = bdsInAppPurchaseInteractor;
    this.billingSerializer = billingSerializer;
    this.appcoinsRewards = appcoinsRewards;
    this.billing = billing;
    this.sharedPreferences = sharedPreferences;
    this.packageManager = packageManager;
  }

  public Single<TransactionBuilder> parseTransaction(String uri, boolean isBds) {
    if (isBds) {
      return bdsInAppPurchaseInteractor.parseTransaction(uri);
    } else {
      return asfInAppPurchaseInteractor.parseTransaction(uri);
    }
  }

  public Completable send(String uri, AsfInAppPurchaseInteractor.TransactionType transactionType,
      String packageName, String productName, String developerPayload, boolean isBds) {
    if (isBds) {
      return bdsInAppPurchaseInteractor.send(uri, transactionType, packageName, productName,
          developerPayload);
    } else {
      return asfInAppPurchaseInteractor.send(uri, transactionType, packageName, productName,
          developerPayload);
    }
  }

  Completable resume(String uri, AsfInAppPurchaseInteractor.TransactionType transactionType,
      String packageName, String productName, String developerPayload, boolean isBds) {
    if (isBds) {
      return bdsInAppPurchaseInteractor.resume(uri, transactionType, packageName, productName,
          developerPayload);
    } else {
      return Completable.error(new UnsupportedOperationException("Asf doesn't support resume."));
    }
  }

  Observable<Payment> getTransactionState(String uri) {
    return Observable.merge(asfInAppPurchaseInteractor.getTransactionState(uri),
        bdsInAppPurchaseInteractor.getTransactionState(uri));
  }

  public Completable remove(String uri) {
    return asfInAppPurchaseInteractor.remove(uri)
        .andThen(bdsInAppPurchaseInteractor.remove(uri));
  }

  public void start() {
    asfInAppPurchaseInteractor.start();
    bdsInAppPurchaseInteractor.start();
  }

  public Observable<List<Payment>> getAll() {
    return Observable.merge(asfInAppPurchaseInteractor.getAll(),
        bdsInAppPurchaseInteractor.getAll());
  }

  List<BigDecimal> getTopUpChannelSuggestionValues(BigDecimal price) {
    return bdsInAppPurchaseInteractor.getTopUpChannelSuggestionValues(price);
  }

  public Single<String> getWalletAddress() {
    return asfInAppPurchaseInteractor.getWalletAddress();
  }

  Single<AsfInAppPurchaseInteractor.CurrentPaymentStep> getCurrentPaymentStep(String packageName,
      TransactionBuilder transactionBuilder) {
    return asfInAppPurchaseInteractor.getCurrentPaymentStep(packageName, transactionBuilder);
  }

  public Single<FiatValue> convertToFiat(double appcValue, String currency) {
    return asfInAppPurchaseInteractor.convertToFiat(appcValue, currency);
  }

  public Single<FiatValue> convertToLocalFiat(double appcValue) {
    return asfInAppPurchaseInteractor.convertToLocalFiat(appcValue);
  }

  public BillingMessagesMapper getBillingMessagesMapper() {
    return bdsInAppPurchaseInteractor.getBillingMessagesMapper();
  }

  private Single<Purchase> getCompletedPurchase(String packageName, String productName) {
    return bdsInAppPurchaseInteractor.getCompletedPurchase(packageName, productName);
  }

  Single<Payment> getCompletedPurchase(Payment transaction, boolean isBds) {
    return parseTransaction(transaction.getUri(), isBds).flatMap(transactionBuilder -> {
      if (isBds && transactionBuilder.getType()
          .equalsIgnoreCase(TransactionData.TransactionType.INAPP.name())) {
        return getCompletedPurchase(transaction.getPackageName(), transaction.getProductId()).map(
            purchase -> mapToBdsPayment(transaction, purchase))
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(payment -> remove(transaction.getUri()).toSingleDefault(payment));
      } else {
        return Single.fromCallable(() -> transaction)
            .flatMap(bundle -> remove(transaction.getUri()).toSingleDefault(bundle));
      }
    });
  }

  private Payment mapToBdsPayment(Payment transaction, Purchase purchase) {
    return new Payment(transaction.getUri(), transaction.getStatus(), purchase.getUid(),
        purchase.getSignature()
            .getValue(), billingSerializer.serializeSignatureData(purchase),
        transaction.getOrderReference());
  }

  public Single<Boolean> isWalletFromBds(String packageName, String wallet) {
    if (packageName == null) {
      return Single.just(false);
    }
    return bdsInAppPurchaseInteractor.getWallet(packageName)
        .map(wallet::equalsIgnoreCase)
        .onErrorReturn(throwable -> false);
  }

  private Single<List<Gateway.Name>> getFilteredGateways(TransactionBuilder transactionBuilder) {
    return Single.zip(getRewardsBalance(), hasAppcoinsFunds(transactionBuilder),
        (creditsBalance, hasAppcoinsFunds) -> getNewPaymentGateways(creditsBalance,
            hasAppcoinsFunds, transactionBuilder.amount()));
  }

  private Single<Boolean> hasAppcoinsFunds(TransactionBuilder transaction) {
    return asfInAppPurchaseInteractor.isAppcoinsPaymentReady(transaction);
  }

  private List<Gateway.Name> getNewPaymentGateways(BigDecimal creditsBalance,
      Boolean hasAppcoinsFunds, BigDecimal amount) {
    List<Gateway.Name> list = new LinkedList<>();

    if (creditsBalance.compareTo(amount) >= 0) {
      list.add(Gateway.Name.appcoins_credits);
    }

    if (hasAppcoinsFunds) {
      list.add(Gateway.Name.appcoins);
    }

    list.add(Gateway.Name.adyen);

    return list;
  }

  private Single<BigDecimal> getRewardsBalance() {
    return appcoinsRewards.getBalance()
        .map(BalanceUtils::weiToEth);
  }

  Single<String> getTransactionUid(String uid) {
    return getCompletedTransaction(uid).map(Transaction::getHash)
        .firstOrError();
  }

  public Single<Price> getTransactionAmount(String uid) {
    return getCompletedTransaction(uid).map(Transaction::getPrice)
        .firstOrError();
  }

  private Single<List<PaymentMethodEntity>> getAvailablePaymentMethods(
      TransactionBuilder transaction, List<PaymentMethodEntity> paymentMethods) {
    return getFilteredGateways(transaction).map(
        filteredGateways -> removeUnavailable(paymentMethods, filteredGateways));
  }

  private Observable<Transaction> getCompletedTransaction(String uid) {
    return getTransaction(uid).filter(transaction -> transaction.getStatus()
        .equals(Status.COMPLETED));
  }

  public Observable<Transaction> getTransaction(String uid) {
    return Observable.interval(0, 5, TimeUnit.SECONDS, Schedulers.io())
        .timeInterval()
        .switchMap(longTimed -> billing.getAppcoinsTransaction(uid, Schedulers.io())
            .toObservable());
  }

  Single<List<PaymentMethod>> getPaymentMethods(TransactionBuilder transaction,
      String transactionValue, String currency) {
    return bdsInAppPurchaseInteractor.getPaymentMethods(transactionValue, currency)
        .flatMap(paymentMethods -> getAvailablePaymentMethods(transaction, paymentMethods).flatMap(
            availablePaymentMethods -> Observable.fromIterable(paymentMethods)
                .map(paymentMethod -> mapPaymentMethods(paymentMethod, availablePaymentMethods))
                .toList())
            .map(this::removePaymentMethods))
        .map(this::swapDisabledPositions);
  }

  private List<PaymentMethod> removePaymentMethods(List<PaymentMethod> paymentMethods) {
    if (hasFunds(paymentMethods) || !hasRequiredAptoideVersionInstalled()) {
      Iterator<PaymentMethod> iterator = paymentMethods.iterator();
      while (iterator.hasNext()) {
        PaymentMethod paymentMethod = iterator.next();
        if (paymentMethod.getId()
            .equals("earn_appcoins")) {
          iterator.remove();
        }
      }
    }
    return paymentMethods;
  }

  private boolean hasRequiredAptoideVersionInstalled() {
    try {
      PackageInfo packageInfo = packageManager.getPackageInfo(BuildConfig.APTOIDE_PKG_NAME, 0);
      return packageInfo.versionCode >= EARN_APPCOINS_APTOIDE_VERCODE;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean hasFunds(List<PaymentMethod> clonedList) {
    for (PaymentMethod paymentMethod : clonedList) {
      if ((paymentMethod.getId()
          .equals(APPC_ID) && paymentMethod.isEnabled())
          || paymentMethod.getId()
          .equals(CREDITS_ID) && paymentMethod.isEnabled()) {
        return true;
      }
    }
    return false;
  }

  List<PaymentMethod> mergeAppcoins(List<PaymentMethod> paymentMethods) {
    PaymentMethod appcMethod = getAppcMethod(paymentMethods);
    PaymentMethod creditsMethod = getCreditsMethod(paymentMethods);
    if (appcMethod != null && creditsMethod != null) {
      return buildMergedList(paymentMethods, appcMethod, creditsMethod);
    }
    return paymentMethods;
  }

  private List<PaymentMethod> buildMergedList(List<PaymentMethod> paymentMethods,
      PaymentMethod appcMethod, PaymentMethod creditsMethod) {
    List<PaymentMethod> mergedList = new ArrayList<>();
    for (PaymentMethod paymentMethod : paymentMethods) {
      if (paymentMethod.getId()
          .equals(APPC_ID)) {
        String mergedId = "merged_appcoins";
        String mergedLabel = appcMethod.getLabel() + " / " + creditsMethod.getLabel();
        boolean isMergedEnabled = appcMethod.isEnabled() || creditsMethod.isEnabled();
        mergedList.add(new AppCoinsPaymentMethod(mergedId, mergedLabel, appcMethod.getIconUrl(),
            isMergedEnabled, appcMethod.isEnabled(), creditsMethod.isEnabled(),
            appcMethod.getLabel(), creditsMethod.getLabel(), creditsMethod.getIconUrl()));
      } else if (!paymentMethod.getId()
          .equals(CREDITS_ID)) {
        //Don't add the credits method to this list
        mergedList.add(paymentMethod);
      }
    }
    return mergedList;
  }

  private PaymentMethod getCreditsMethod(List<PaymentMethod> paymentMethods) {
    for (PaymentMethod paymentMethod : paymentMethods) {
      if (paymentMethod.getId()
          .equals(CREDITS_ID)) {
        return paymentMethod;
      }
    }
    return null;
  }

  private PaymentMethod getAppcMethod(List<PaymentMethod> paymentMethods) {
    for (PaymentMethod paymentMethod : paymentMethods) {
      if (paymentMethod.getId()
          .equals(APPC_ID)) {
        return paymentMethod;
      }
    }
    return null;
  }

  private List<PaymentMethod> swapDisabledPositions(List<PaymentMethod> paymentMethods) {
    boolean swapped = false;
    if (paymentMethods.size() > 1) {
      for (int position = 1; position < paymentMethods.size(); position++) {
        if (shouldSwap(paymentMethods, position)) {
          Collections.swap(paymentMethods, position, position - 1);
          swapped = true;
          break;
        }
      }
      if (swapped) {
        swapDisabledPositions(paymentMethods);
      }
    }
    return paymentMethods;
  }

  private boolean shouldSwap(List<PaymentMethod> paymentMethods, int position) {
    return paymentMethods.get(position)
        .isEnabled() && !paymentMethods.get(position - 1)
        .isEnabled();
  }

  private List<PaymentMethodEntity> removeUnavailable(List<PaymentMethodEntity> paymentMethods,
      List<Gateway.Name> filteredGateways) {
    List<PaymentMethodEntity> clonedPaymentMethods = new ArrayList<>(paymentMethods);
    Iterator<PaymentMethodEntity> iterator = clonedPaymentMethods.iterator();

    while (iterator.hasNext()) {
      PaymentMethodEntity paymentMethod = iterator.next();
      String id = paymentMethod.getId();
      if (id.equals(APPC_ID) && !filteredGateways.contains(Gateway.Name.appcoins)) {
        iterator.remove();
      } else if (id.equals(CREDITS_ID) && !filteredGateways.contains(
          Gateway.Name.appcoins_credits)) {
        iterator.remove();
      } else if (paymentMethod.getGateway() != null && (paymentMethod.getGateway()
          .getName() == (Gateway.Name.myappcoins)
          || paymentMethod.getGateway()
          .getName() == (Gateway.Name.adyen)) && isUnavailable(paymentMethod)) {
        iterator.remove();
      }
    }
    return clonedPaymentMethods;
  }

  private PaymentMethod mapPaymentMethods(PaymentMethodEntity paymentMethod,
      List<PaymentMethodEntity> availablePaymentMethods) {
    for (PaymentMethodEntity availablePaymentMethod : availablePaymentMethods) {
      if (paymentMethod.getId()
          .equals(availablePaymentMethod.getId())) {
        return new PaymentMethod(paymentMethod.getId(), paymentMethod.getLabel(),
            paymentMethod.getIconUrl(), true);
      }
    }
    return new PaymentMethod(paymentMethod.getId(), paymentMethod.getLabel(),
        paymentMethod.getIconUrl(), false);
  }

  boolean hasPreSelectedPaymentMethod() {
    return sharedPreferences.contains(PRE_SELECTED_PAYMENT_METHOD_KEY);
  }

  String getPreSelectedPaymentMethod() {
    return sharedPreferences.getString(PRE_SELECTED_PAYMENT_METHOD_KEY,
        PaymentMethodsView.PaymentMethodId.APPC_CREDITS.getId());
  }

  boolean hasAsyncLocalPayment() {
    return sharedPreferences.contains(LOCAL_PAYMENT_METHOD_KEY);
  }

  public void savePreSelectedPaymentMethod(String paymentMethod) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(PRE_SELECTED_PAYMENT_METHOD_KEY, paymentMethod);
    editor.putString(LAST_USED_PAYMENT_METHOD_KEY, paymentMethod);
    editor.apply();
  }

  public void saveAsyncLocalPayment(String paymentMethod) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(LOCAL_PAYMENT_METHOD_KEY, paymentMethod);
    editor.apply();
  }

  public void removePreSelectedPaymentMethod() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(PRE_SELECTED_PAYMENT_METHOD_KEY);
    editor.apply();
  }

  public void removeAsyncLocalPayment() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(LOCAL_PAYMENT_METHOD_KEY);
    editor.apply();
  }

  String getLastUsedPaymentMethod() {
    return sharedPreferences.getString(LAST_USED_PAYMENT_METHOD_KEY,
        PaymentMethodsView.PaymentMethodId.CREDIT_CARD.getId());
  }

  private boolean isUnavailable(PaymentMethodEntity paymentMethod) {
    return paymentMethod.getAvailability()
        .equals("UNAVAILABLE");
  }
}
