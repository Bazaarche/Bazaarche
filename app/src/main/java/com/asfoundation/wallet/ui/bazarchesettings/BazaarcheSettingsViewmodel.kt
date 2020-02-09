package com.asfoundation.wallet.ui.bazarchesettings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asfoundation.wallet.util.languagecontroller.Language
import com.asfoundation.wallet.util.languagecontroller.LanguageController

class BazaarcheSettingsViewModel : ViewModel() {


  private val _restartObservable = MutableLiveData<Boolean>()
  internal val restartObservable: LiveData<Boolean> = _restartObservable

  internal fun getSelectedLanguagePosition(): Int {
    return Language.values().indexOfFirst {
      it == LanguageController.getInstance().getLanguage()
    }
  }

  fun onLanguageSelected(context: Context, selectedPosition: Int) {

    val language = Language.values()[selectedPosition]

    if (language != LanguageController.getInstance().getLanguage()) {
      LanguageController.getInstance().setLocale(context, language)

      _restartObservable.value = true
    }
  }


}