package com.asfoundation.wallet.navigator

import android.content.Context
import android.content.Intent
import com.asfoundation.wallet.ui.bazarchesettings.BazaarcheSettingsActivity
import javax.inject.Inject

class CatalogViewNavigator @Inject constructor() {

  fun openSettings(context: Context) {
    val intent = Intent(context, BazaarcheSettingsActivity::class.java)
    context.startActivity(intent)
  }

}


