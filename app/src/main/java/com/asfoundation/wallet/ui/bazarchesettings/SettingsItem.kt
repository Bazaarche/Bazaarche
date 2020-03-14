package com.asfoundation.wallet.ui.bazarchesettings

import androidx.annotation.StringRes
import com.asf.wallet.R

internal enum class SettingsItem(@StringRes val resId: Int) {

  ACTION_WALLETS(R.string.action_wallets),
  TRANSACTIONS_LIST(R.string.transactions_list),
  LANGUAGE_SETTINGS(R.string.language_settings),
  BAZAARCHE_GUIDE(R.string.bazaarche_guide),
  SUPPORT(R.string.support)
}

internal val items = arrayOf(
    SettingsItem.ACTION_WALLETS,
    SettingsItem.TRANSACTIONS_LIST,
    SettingsItem.LANGUAGE_SETTINGS,
    SettingsItem.BAZAARCHE_GUIDE,
    SettingsItem.SUPPORT
)
