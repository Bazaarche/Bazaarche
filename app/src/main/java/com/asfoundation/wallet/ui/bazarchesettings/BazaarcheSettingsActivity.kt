package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asf.wallet.R
import kotlinx.android.synthetic.main.layout_app_bar.*

class BazaarcheSettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bazaarche_settings)
    setupActionbar()

    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, BazaarcheSettingsFragment())
        .commit()
  }

  private fun setupActionbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }
}
