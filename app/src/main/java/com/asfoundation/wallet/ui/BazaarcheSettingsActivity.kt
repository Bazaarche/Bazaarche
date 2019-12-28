package com.asfoundation.wallet.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.asf.wallet.R
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class BazaarcheSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bazaarche_settings)
        toolbar()
    }


}