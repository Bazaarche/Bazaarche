package com.asfoundation.wallet.util.languagecontroller

enum class Language(val languageString: String) {
  PERSIAN("fa"),
  ENGLISH("en");

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