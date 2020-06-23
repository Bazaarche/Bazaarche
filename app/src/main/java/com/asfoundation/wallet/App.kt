package com.asfoundation.wallet

import android.app.Activity
import android.content.Intent
import androidx.multidex.MultiDexApplication
import androidx.work.*
import com.appcoins.wallet.appcoins.rewards.AppcoinsRewards
import com.appcoins.wallet.bdsbilling.ProxyService
import com.appcoins.wallet.bdsbilling.WalletService
import com.appcoins.wallet.bdsbilling.repository.BdsApiSecondary
import com.appcoins.wallet.bdsbilling.repository.RemoteRepository.BdsApi
import com.appcoins.wallet.billing.BillingDependenciesProvider
import com.appcoins.wallet.billing.BillingMessagesMapper
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.analytics.RakamAnalytics
import com.asfoundation.wallet.di.AppComponent
import com.asfoundation.wallet.di.DaggerAppComponent
import com.asfoundation.wallet.identification.IdsRepository
import com.asfoundation.wallet.logging.CrashlyticsReceiver
import com.asfoundation.wallet.logging.FlurryReceiver
import com.asfoundation.wallet.logging.Logger
import com.asfoundation.wallet.logging.SentryReceiver
import com.asfoundation.wallet.poa.ProofOfAttentionService
import com.asfoundation.wallet.repository.PreferencesDataSource
import com.asfoundation.wallet.support.SupportNotificationWorker
import com.asfoundation.wallet.ui.iab.AppcoinsOperationsDataSaver
import com.asfoundation.wallet.ui.iab.InAppPurchaseInteractor
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.flurry.android.FlurryAgent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import io.fabric.sdk.android.services.common.QueueFile
import io.intercom.android.sdk.Intercom
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App : MultiDexApplication(), HasAndroidInjector, BillingDependenciesProvider {
  @Inject
  lateinit var androidInjector: DispatchingAndroidInjector<Any>
  @Inject
  lateinit var preferences : PreferencesDataSource
  @Inject
  lateinit var proofOfAttentionService: ProofOfAttentionService
  @Inject
  lateinit var inAppPurchaseInteractor: InAppPurchaseInteractor
  @Inject
  lateinit var appcoinsOperationsDataSaver: AppcoinsOperationsDataSaver
  @Inject
  lateinit var bdsApi: BdsApi
  @Inject
  lateinit var walletService: WalletService
  @Inject
  lateinit var proxyService: ProxyService
  @Inject
  lateinit var appcoinsRewards: AppcoinsRewards
  @Inject
  lateinit var billingMessagesMapper: BillingMessagesMapper
  @Inject
  lateinit var bdsapiSecondary: BdsApiSecondary
  @Inject
  lateinit var idsRepository: IdsRepository
  @Inject
  lateinit var logger: Logger
  @Inject
  lateinit var rakamAnalytics: RakamAnalytics

  companion object {
    private val TAG = App::class.java.name
  }

  override fun onCreate() {
    super.onCreate()
    val appComponent = DaggerAppComponent.builder()
        .application(this)
        .build()
    appComponent.inject(this)
    setupRxJava()
    setupWorkManager(appComponent)
    setupSupportNotificationWorker()
    initiateFlurry()
    initiateCrashlytics()
    startBazaarcheSetup(this, preferences);
    inAppPurchaseInteractor.start()
    proofOfAttentionService.start()
    appcoinsOperationsDataSaver.start()
    appcoinsRewards.start()
    rakamAnalytics.start()
    initiateIntercom()
//    initiateSentry()
  }

  private fun setupRxJava() {
    RxJavaPlugins.setErrorHandler { throwable: Throwable ->
      if (throwable is UndeliverableException) {
        if (BuildConfig.DEBUG) {
          throwable.printStackTrace()
        } else {
          logger.log(TAG, throwable)
        }
      } else {
        throw RuntimeException(throwable)
      }
    }
  }

  private fun setupWorkManager(appComponent: AppComponent) {
    WorkManager.initialize(this,
        Configuration.Builder().setWorkerFactory(appComponent.daggerWorkerFactory()).build())
  }

  private fun setupSupportNotificationWorker() {
    val workerConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val notificationWorkRequest = PeriodicWorkRequest.Builder(SupportNotificationWorker::class.java,
        SupportNotificationWorker.NOTIFICATION_PERIOD, TimeUnit.MINUTES)
        .addTag(SupportNotificationWorker.WORKER_TAG)
        .setConstraints(workerConstraints)
        .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(SupportNotificationWorker.UNIQUE_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest)
  }

  private fun initiateCrashlytics() {
    Fabric.with(this, Crashlytics.Builder().core(
        CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build())
    logger.addReceiver(CrashlyticsReceiver())
  }

  private fun initiateFlurry() {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.Builder()
          .withLogEnabled(false)
          .build(this, BuildConfig.FLURRY_APK_KEY)
      logger.addReceiver(FlurryReceiver())
    }
  }

  private fun initiateSentry() {
    Sentry.init(BuildConfig.SENTRY_DSN_KEY, AndroidSentryClientFactory(this))
    logger.addReceiver(SentryReceiver())
  }

  private fun initiateIntercom() {
    Intercom.initialize(this, BuildConfig.INTERCOM_API_KEY, BuildConfig.INTERCOM_APP_ID)
    Intercom.client()
        .setInAppMessageVisibility(Intercom.Visibility.GONE)
  }

  override fun androidInjector() = androidInjector

  override fun supportedVersion() = BuildConfig.BILLING_SUPPORTED_VERSION

  override fun bdsApi() = bdsApi

  override fun walletService() = walletService

  override fun proxyService()= proxyService

  override fun billingMessagesMapper()= billingMessagesMapper

  override fun bdsApiSecondary() = bdsapiSecondary
}