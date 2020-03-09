package com.asfoundation.wallet.ui.iab.bazaariab

import android.os.Bundle

sealed class PurchaseState {

  data class Purchased(val purchaseData: Bundle) : PurchaseState()
  object InProgress : PurchaseState()
  object BazaarNotFoundError : PurchaseState()
  data class Error(val errorBundle: Bundle) : PurchaseState()
}