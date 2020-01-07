package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import com.asf.wallet.R
import com.asfoundation.wallet.ui.BaseActivity

class BazaarcheSettingsActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bazaarche_settings)
    toolbar()

    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, BazarcheSettingsFragment())
        .commit()
  }

}
