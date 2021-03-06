package com.asfoundation.wallet.repository

import android.location.Location
import com.asfoundation.wallet.entity.DeviceInfo
import com.asfoundation.wallet.entity.RequestProperties
import com.asfoundation.wallet.ui.ThemeState
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RequestPropertiesDataSource @Inject constructor(private val deviceInfoService: DeviceInfoService,
                                                      private val preferencesDataSource: PreferencesDataSource) {

  fun getRequestProperties(lastKnownLocation: Location? = null): RequestProperties {
    return RequestProperties(
        clientID = DeviceInfoService.CLIENT_ID,
        clientVersion = deviceInfoService.clientVersion,
        clientVersionCode = deviceInfoService.bazaarcheVersionCode,
        language = deviceInfoService.language.value,
        isKidsEnabled = false,
        androidClientInfo = DeviceInfo(
            sdkVersion = deviceInfoService.sdkVersion,
            model = deviceInfoService.model,
            product = deviceInfoService.product,
            manufacturer = deviceInfoService.manufacturer,
            osBuild = "", // don't know what is this, not used in dilmaj
            hardware = "", // don't know what is this, not used in dilmaj
            device = "", // don't know what is this, not used in dilmaj
            mnc = deviceInfoService.simCardMnc,
            mcc = deviceInfoService.simCardMcc,
            locale = "", // don't know what is this, not used in dilmaj
            city = "NA",
            province = "NA",
            country = "NA",
            cpu = deviceInfoService.cpu,
            dpi = deviceInfoService.dpi,
            width = deviceInfoService.width,
            height = deviceInfoService.height,
            adId = preferencesDataSource.adId,
            adOptOut = false,
            androidId = deviceInfoService.getAndroidId()
        ),
        lat = lastKnownLocation?.latitude,
        lon = lastKnownLocation?.longitude,
        appThemeState = getAppThemeState().value
    )
  }

  private fun getAppThemeState(): ThemeState {
    return ThemeState.LIGHT_THEME
  }

}