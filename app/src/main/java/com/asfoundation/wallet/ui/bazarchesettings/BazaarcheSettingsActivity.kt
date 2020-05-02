package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asf.wallet.R

class BazaarcheSettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bazaarche_settings)

    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, BazaarcheSettingsFragment())
        .commit()
  }

}
