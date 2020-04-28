package com.asfoundation.wallet.ui.bazarchesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModelFactory<VM : ViewModel>(private val clazz: Class<VM>,
                                                    private val provideViewModel: () -> VM) :
    ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {

    if (modelClass.isAssignableFrom(clazz)) {

      @Suppress("UNCHECKED_CAST")
      return provideViewModel() as T
    }

    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}