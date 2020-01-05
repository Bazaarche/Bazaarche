package com.asfoundation.wallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.asfoundation.wallet.catalog.repository.CatalogRepository
import com.asfoundation.wallet.entity.Row
import com.asfoundation.wallet.ui.catalog.CatalogItem
import com.asfoundation.wallet.ui.catalog.toCatalogItems
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CatalogViewModel(private val catalogRepository: CatalogRepository) : BaseViewModel() {


  init {
    loadCatalog()
  }

  private val catalogRows = MutableLiveData<List<CatalogItem>>()

  fun getCatalogRows(): LiveData<List<CatalogItem>> = catalogRows


  private fun loadCatalog() {

    disposable = catalogRepository.loadCatalogRows()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(::catalogItemMapper)
        .subscribe({
          catalogRows.value = it
        }, {
          it.printStackTrace()
        })
  }

  private fun catalogItemMapper(rows: List<Row>): List<CatalogItem> {

    return rows.flatMap(Row::toCatalogItems)
  }
}
