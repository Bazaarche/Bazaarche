package com.asfoundation.wallet.ui.iab

open class PaymentMethod(open val id: String, open val label: String,
                         open val iconUrl: String, open val isEnabled: Boolean = true) {
  constructor() : this("", "", "", false)

  companion object {
    @JvmField
    val APPC: PaymentMethod =
        PaymentMethod("appcoins", "AppCoins (APPC)",
            "https://cdn6.aptoide.com/imgs/a/f/9/af95bd0d14875800231f05dbf1933143_logo.png")
  }
}

