package com.asfoundation.wallet.ui.catalog

import com.asfoundation.wallet.entity.Row

sealed class CatalogItem

data class Header(val title: String, val more: String) : CatalogItem()

data class AppItem(val packageName: String, val name: String, val image: String) : CatalogItem()

fun Row.toCatalogItems(): List<CatalogItem> {

  val catalogItems = mutableListOf<CatalogItem>()
  catalogItems.add(Header(title, more))

  appList!!.appList.mapTo(catalogItems) {
    AppItem(it.packageName, it.name, it.image)
  }
  return catalogItems

}
