package com.asfoundation.wallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.asfoundation.wallet.catalog.repository.CatalogRepository
import com.asfoundation.wallet.entity.Hami
import com.asfoundation.wallet.entity.Row
import com.asfoundation.wallet.ui.catalog.CatalogItem
import com.asfoundation.wallet.ui.catalog.toCatalogItems
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CatalogViewModel(private val catalogRepository: CatalogRepository) : BaseViewModel() {


  init {
    loadCatalog()
  }

  private val catalogRows = MutableLiveData<List<CatalogItem>>()
  private val hami = MutableLiveData<Hami>()

  fun getCatalogRows(): LiveData<List<CatalogItem>> = catalogRows

  fun getHami(): LiveData<Hami> = hami


  private fun loadCatalog() {

    disposable = catalogRepository.loadCatalogRows()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap {
          hami.value = it[0].hamiPromo
          Single.just(it.drop(1))
        }
        .map {
          it.flatMap(Row::toCatalogItems)
        }
        .subscribe({
          catalogRows.value = it
        }, {
          it.printStackTrace()
        })
  }
}
