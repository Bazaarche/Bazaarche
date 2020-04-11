package com.asfoundation.wallet.repository

import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.viewmodel.AutoUpdateModel
import io.reactivex.Single
import javax.inject.Inject

class LocalAutoUpdateService @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource) {

  fun loadAutoUpdateModel(): Single<AutoUpdateModel> {
    val lowestSupportedVersion = preferencesDataSource.lowestSupportedVersion

    val blackList = if (BuildConfig.VERSION_CODE < lowestSupportedVersion) {
      listOf(BuildConfig.VERSION_CODE)
    } else {
      emptyList()
    }

    val autoUpdateModel = AutoUpdateModel(
        updateVersionCode = lowestSupportedVersion,
        blackList = blackList)

    return Single.just(autoUpdateModel)
  }

  fun saveLowestSupportedVersion(lowestSupportedVersion: Int) {
    preferencesDataSource.lowestSupportedVersion = lowestSupportedVersion
  }

}