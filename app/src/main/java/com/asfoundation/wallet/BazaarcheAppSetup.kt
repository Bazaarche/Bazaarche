package com.asfoundation.wallet

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.asfoundation.wallet.fcm.FcmService
import com.asfoundation.wallet.repository.PreferencesDataSource
import com.asfoundation.wallet.util.languagecontroller.Language
import com.asfoundation.wallet.util.languagecontroller.LanguageController
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

fun startBazaarcheSetup(application: Application, preferencesDataSource: PreferencesDataSource) {
  createNotificationChannel(application)

  checkAdId(application, preferencesDataSource)

  LanguageController.init(application, Language.PERSIAN)
}

fun createNotificationChannel(context: Context) {

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel(FcmService.NOTIFICATION_CHANNEL_ID,
        FcmService.NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT)

    val notificationManager =
        ContextCompat.getSystemService(context, NotificationManager::class.java)
    notificationManager?.createNotificationChannel(channel)
  }

}

@SuppressLint("CheckResult")
private fun checkAdId(context: Context, preferencesDataSource: PreferencesDataSource) {

  fun determineAdId(context: Context): String {
    val advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
    return advertisingIdInfo.id
  }

  Completable.fromRunnable {
    val adId = determineAdId(context)
    preferencesDataSource.adId = adId
  }
      .onErrorComplete()
      .subscribeOn(Schedulers.io())
      .subscribe()

}

