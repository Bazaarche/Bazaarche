package com.asfoundation.wallet.util

import android.net.Uri
import com.asf.wallet.BuildConfig

class Parameters {
  companion object {
    const val VALUE = "value"
    const val TO = "to"
    const val PRODUCT = "product"
    const val DOMAIN = "domain"
    const val DATA = "data"
    const val CURRENCY = "currency"
    const val CALLBACK_URL = "callback_url"
    const val SCHEME = "https"
    const val HOST = BuildConfig.PAYMENT_HOST
    const val PATH = "/transaction"
    const val PAYMENT_TYPE_INAPP_UNMANAGED = "inapp_unmanaged"
    const val NETWORK_ID_ROPSTEN = 3L
    const val NETWORK_ID_MAIN = 1L
  }
}

fun Uri.isOneStepURLString() = scheme == Parameters.SCHEME && host == Parameters.HOST && path.startsWith(
    Parameters.PATH)

fun parseOneStep(uri: Uri) = OneStepUri().apply {
  scheme = uri.scheme
  host = uri.host
  path = uri.path
  parameters.apply {
    for (key in uri.queryParameterNames) {
      this[key] = uri.getQueryParameter(key)
    }
  }
}