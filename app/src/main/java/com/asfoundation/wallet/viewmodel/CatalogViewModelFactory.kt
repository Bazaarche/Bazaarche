package com.asfoundation.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.asfoundation.wallet.catalog.repository.CatalogRepository
import javax.inject.Inject

class CatalogViewModelFactory @Inject constructor(private val catalogRepository: CatalogRepository)
  : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
      return CatalogViewModel(catalogRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}