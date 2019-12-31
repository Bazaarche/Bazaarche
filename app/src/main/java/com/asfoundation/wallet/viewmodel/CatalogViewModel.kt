package com.asfoundation.wallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.asfoundation.wallet.catalog.repository.CatalogRepository
import com.asfoundation.wallet.entity.Row
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CatalogViewModel(private val catalogRepository: CatalogRepository) : BaseViewModel() {


  init {
    loadCatalogRows()
  }

  private val catalogRows = MutableLiveData<List<Row>>()

  fun getCatalogRows(): LiveData<List<Row>> = catalogRows


  private fun loadCatalogRows() {

    disposable = catalogRepository.loadCatalogRows()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          catalogRows.value = it
        }, {
          it.printStackTrace()
        })
  }
}
