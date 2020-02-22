package com.asfoundation.wallet.repository

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.core.net.ConnectivityManagerCompat
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.util.languagecontroller.Language
import com.asfoundation.wallet.util.languagecontroller.LanguageController
import java.util.UUID
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
  fun getClientId(): String = "05701d5cd23f43ff82bd7e440d3429be"
//    fun getClientId(randomUuidGenerator: () -> UUID = UUID::randomUUID): String {
//        var clientId = "hfCuKMmjSGm7E5xfKYX2fA"
//        if (clientId.isEmpty()) {
//            val uuid = randomUuidGenerator()
//            val uuidArr = asBytes(uuid)
//            clientId = Base64.encodeWebSafe(uuidArr, false)
//            if (clientId.length > DEVICE_ID_MAX_LENGTH) {
//                clientId = clientId.substring(0, DEVICE_ID_MAX_LENGTH)
//            }
//            settingsRepository.setClientId(clientId)
//        }
//        return clientId
//    }

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

  fun isDeviceThemeDark(): Boolean {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
  }

  fun getNetworkType(): String = context.getNetworkType()
  @SuppressLint("HardwareIds")
  fun getAndroidId(): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
  }

  fun getNetworkOperator(): String = context.getNetworkOperator()
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

  private fun Context.getNetworkType(): String {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    telephonyManager?.run {
      if (isOnWifi()) {
        return "WIFI"
      }
      return try {
        when (networkType) {
          TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
          TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
          TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
          TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD"
          TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO rev. 0"
          TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO rev. A"
          TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO rev. B"
          TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
          TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
          TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
          TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
          TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
          TelephonyManager.NETWORK_TYPE_IDEN -> "iDen"
          TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
          TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
          TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Unknown"
          else -> "Unknown"
        }
      } catch (e: Exception) {
        // NoSuchMethodError
        "Unknown"
      }
    }
    return "Unknown"
  }

  private fun Context.getNetworkOperator(): String {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    telephonyManager?.run {
      var networkOperator: String? = telephonyManager.networkOperator
      if (networkOperator != null) {
        when (networkOperator) {
          "43220" -> networkOperator = "Rightel"
          "43235" -> networkOperator = "Irancell"
          "43211" -> networkOperator = "MCI"
          "43270" -> networkOperator = "TCI"
          "43232" -> networkOperator = "Taliya"
          "26207" -> networkOperator = "O2_Germany"
          "42402" -> networkOperator = "Etisalat"
          "28601" -> networkOperator = "Turkcell"
        }
      } else {
        networkOperator = "unknown"
      }
      return networkOperator
    }
    return "unknown"
  }

  private fun Context.isOnWifi(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return if (connectivityManager != null) {
      !ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager)
    } else {
      false
    }
  }

  companion object {
    const val DEVICE_ID_MAX_LENGTH = 31
    const val TAG = "DeviceInfoDataSource"
    const val APP_NAME = "web"//TODO: convert to bazaarche when fixed by backend
  }
}