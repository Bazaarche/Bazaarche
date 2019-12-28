package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import com.asf.wallet.R
import com.asfoundation.wallet.ui.BaseActivity

class BazaarcheSettingsActivity : BaseActivity(), InteractionListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bazaarche_settings)
    toolbar()

    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, BazarcheSettingsFragment())
        .commit()
  }

  override fun onTransactionsClicked() {
    //TODO
  }

  override fun onLanguageSettingsClicked() {
    //TODO
  }

  override fun onGuideClicked() {
    //TODO
  }

  override fun onSupportClicked() {
    //TODO
  }

}