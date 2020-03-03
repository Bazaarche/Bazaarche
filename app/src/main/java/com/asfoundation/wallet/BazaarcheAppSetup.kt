package com.asfoundation.wallet

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.asfoundation.wallet.repository.PreferencesDataSource
import com.asfoundation.wallet.util.languagecontroller.LanguageController
import com.asfoundation.wallet.util.languagecontroller.Language
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

fun startSetup(application: Application, preferencesDataSource: PreferencesDataSource) {

  checkAdId(application, preferencesDataSource)

  LanguageController.init(application, Language.PERSIAN)

}

@SuppressLint("CheckResult")
private fun checkAdId(context: Context, preferencesDataSource: PreferencesDataSource) {

  fun isAdIdEmpty(preferencesDataSource: PreferencesDataSource): Boolean {
    return preferencesDataSource.adId.isEmpty()
  }

  fun determineAdId(context: Context): String {
    val advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
    return advertisingIdInfo.id
  }

  Completable.fromRunnable {
    if (isAdIdEmpty(preferencesDataSource)) {
      val adId = determineAdId(context)
      preferencesDataSource.adId = adId
    }
  }
      .onErrorComplete()
      .subscribeOn(Schedulers.io())
      .subscribe()

}

