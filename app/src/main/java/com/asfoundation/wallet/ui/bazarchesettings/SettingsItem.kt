package com.asfoundation.wallet.ui.bazarchesettings

import androidx.annotation.StringRes
import com.asf.wallet.R

internal enum class SettingsItem(@StringRes val resId: Int) {

  ACTION_WALLET(R.string.wallet),
  TRANSACTIONS_LIST(R.string.transactions_list),
  LANGUAGE_SETTINGS(R.string.language_settings),
  BAZAARCHE_GUIDE(R.string.bazaarche_guide),
  SUPPORT(R.string.support)
}

internal val items = arrayOf(
    SettingsItem.ACTION_WALLET,
    SettingsItem.TRANSACTIONS_LIST,
    SettingsItem.LANGUAGE_SETTINGS,
    SettingsItem.BAZAARCHE_GUIDE,
    SettingsItem.SUPPORT
)
