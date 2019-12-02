package com.asfoundation.wallet.util.languagecontroller

import android.content.SharedPreferences
import java.util.*

class PreferenceLocaleStore @JvmOverloads constructor(
    private val prefs: SharedPreferences,
    private val defaultLocale: Locale = Locale.getDefault()) : LocaleStore {


  override fun getLocale(): Locale {
    return if (prefs.contains(LANGUAGE_KEY)) {
      val language = prefs.getString(LANGUAGE_KEY, null)!!
      Locale(language)
    } else {
      defaultLocale
    }
  }

  override fun persistLocale(locale: Locale) {
    prefs.edit().putString(LANGUAGE_KEY, locale.language).apply()
  }

  companion object {
    private const val LANGUAGE_KEY = "language_key"
  }
}