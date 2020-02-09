package com.asfoundation.wallet.util.languagecontroller

import androidx.annotation.StringRes
import com.asf.wallet.R

enum class Language(val languageString: String, @StringRes val titleRes: Int) {
  PERSIAN("fa", R.string.persian),
  ENGLISH("en", R.string.english);

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