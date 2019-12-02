package com.asfoundation.wallet.util.languagecontroller

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration

internal class LanguageControllerApplicationCallbacks(private val context: Context,
                                                      private val languageController: LanguageController) : ComponentCallbacks {

  override fun onConfigurationChanged(newConfig: Configuration) {
    languageController.setLocaleInternal(context)
  }

  override fun onLowMemory() {}
}