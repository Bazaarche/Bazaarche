package com.asfoundation.wallet.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.util.languagecontroller.LanguageController
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DeviceInfoService @Inject constructor(private val context: Context) {

  private val metrics = DisplayMetrics().apply {
    val windowService = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowService.defaultDisplay.getMetrics(this)
  }

  val width: Int by lazy { metrics.widthPixels }
  val height: Int by lazy { metrics.heightPixels }
  val clientVersion = APP_NAME
  val bazaarcheVersionCode: Long = BuildConfig.VERSION_CODE.toLong()
  val language = LanguageController.getInstance().getLanguage()
  val sdkVersion = Build.VERSION.SDK_INT
  val model: String = Build.MODEL ?: "UNKNOWN"
  val product: String = Build.PRODUCT ?: "UNKNOWN"
  val manufacturer: String = Build.MANUFACTURER ?: "UNKNOWN"

  /**
   * @return [mcc, mnc]
   */
  private val simNetworkDetails: IntArray by lazy {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val networkOperator = telephonyManager.networkOperator
    try {
      if (networkOperator != null && networkOperator.length > 3) {
        return@lazy intArrayOf(
            Integer.parseInt(networkOperator.substring(0, 3)),
            Integer.parseInt(networkOperator.substring(3))
        )
      }
    } catch (e: NumberFormatException) {
      Log.w(TAG, "telephony manager : network", e)
    }
    return@lazy intArrayOf(0, 0)
  }
  val simCardMcc: Int by lazy {
    if (simNetworkDetails.isNotEmpty()) simNetworkDetails[0] else 0
  }
  val simCardMnc: Int by lazy {
    if (simNetworkDetails.size > 1) simNetworkDetails[1] else 0
  }

  @SuppressLint("HardwareIds")
  fun getAndroidId(): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
  }

  val cpu: String by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Build.SUPPORTED_ABIS.joinToString(",")
    } else {
      @Suppress("DEPRECATION")
      Build.CPU_ABI
    }
  }
  val dpi: Int by lazy {
    metrics.densityDpi
  }

  companion object {
    const val TAG = "DeviceInfoDataSource"
    const val APP_NAME = "web"//TODO: convert to bazaarche when fixed by backend
    const val CLIENT_ID = "iNnISiq2TGqk8KnQryUIsw"
  }
}