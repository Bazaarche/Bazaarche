package com.asfoundation.wallet.catalog.repository

import com.asfoundation.wallet.entity.Row
import io.reactivex.Single
import javax.inject.Inject

class CatalogRepository @Inject constructor(private val catalogService: CatalogService) {

  fun loadCatalogRows(): Single<List<Row>> {
    return catalogService.getCatalogRows()
  }

}