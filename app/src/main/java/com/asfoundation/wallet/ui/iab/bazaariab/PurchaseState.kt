package com.asfoundation.wallet.ui.iab.bazaariab

import android.os.Bundle

sealed class PurchaseState(val isLoading: Boolean = false) {

  data class Purchased(val purchaseData: Bundle) : PurchaseState()
  object InProgress : PurchaseState(isLoading = true)
  object BazaarNotFoundError : PurchaseState()
  data class Error(val errorBundle: Bundle) : PurchaseState()
}