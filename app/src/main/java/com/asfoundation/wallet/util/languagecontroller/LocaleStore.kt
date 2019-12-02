package com.asfoundation.wallet.util.languagecontroller

import java.util.*


interface LocaleStore {
  fun getLocale(): Locale
  fun persistLocale(locale: Locale)
}