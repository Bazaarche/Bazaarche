package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asf.wallet.R
import kotlinx.android.synthetic.main.layout_settings_toolbar.*

class BazaarcheSettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bazaarche_settings)

    setupActionBar()
    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, BazaarcheSettingsFragment())
        .commit()
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  private fun setupActionBar() {
    setSupportActionBar(toolbarSettings)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
  }

}
