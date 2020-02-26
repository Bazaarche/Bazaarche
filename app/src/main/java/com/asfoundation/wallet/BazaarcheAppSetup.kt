@file:JvmName("BazaarcheAppSetup")

package com.asfoundation.wallet

import android.annotation.SuppressLint
import android.content.Context
import com.asfoundation.wallet.repository.PreferencesDataSource
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

fun startSetup(context: Context, preferencesDataSource: PreferencesDataSource) {

  checkAdId(context, preferencesDataSource)
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

