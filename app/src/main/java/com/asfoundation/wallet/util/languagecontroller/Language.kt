package com.asfoundation.wallet.util.languagecontroller

import androidx.annotation.StringRes
import com.asf.wallet.R

enum class Language(val languageString: String, @StringRes val titleRes: Int, val value: Int) {
  ENGLISH("en", R.string.english, 1),
  PERSIAN("fa", R.string.persian, 2);

  companion object {

    fun of(languageString: String): Language {

      return if (languageString == PERSIAN.languageString) {
        PERSIAN
      } else {
        ENGLISH
      }
    }
  }
}