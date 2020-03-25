package com.asfoundation.wallet.ui.iab.bazaariab

import android.os.Bundle
import androidx.annotation.StringRes
import com.asf.wallet.R

sealed class PurchaseState {

  data class Purchased(val purchaseData: Bundle) : PurchaseState()
  object InProgress : PurchaseState()
  object BazaarNotFoundError : PurchaseState()
  data class Canceled(val cancelBundle: Bundle) : PurchaseState()

  open class Error(val errorBundle: Bundle,
                   @StringRes val textRes: Int = R.string.activity_iab_error_message) :
      PurchaseState()
  class NetworkError(errorBundle: Bundle) : Error(errorBundle, R.string.iab_network_error_message)
}