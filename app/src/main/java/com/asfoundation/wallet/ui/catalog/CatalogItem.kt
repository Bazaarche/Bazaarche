package com.asfoundation.wallet.ui.catalog

import com.asfoundation.wallet.entity.AppInfo
import com.asfoundation.wallet.entity.HamiData
import com.asfoundation.wallet.entity.PromoListResponse
import com.asfoundation.wallet.entity.Row

sealed class CatalogItem


data class Hami(val title: String, val shortDescription: String, val link: String,
                val imageURL: String, val appInfo: AppInfo) : CatalogItem() {

  companion object {
    fun from(hamiData: HamiData): Hami = hamiData.run {
      Hami(title, shortDescription, link, imageURL, appInfo)
    }
  }
}

data class PromoItem(val title: String, val link: String, val imageURL: String): CatalogItem() {

  companion object {
    fun from(promo: PromoListResponse.Promo): PromoItem = promo.run {
      PromoItem(info.title, info.link, info.image)
    }
  }
}

data class Header(val title: String, val more: String) : CatalogItem()

data class AppItem(val packageName: String, val name: String, val image: String) : CatalogItem()

fun Row.toCatalogItems(): List<CatalogItem> {

  return when {
    hamiItem != null -> {

      listOf(Hami.from(hamiItem))
    }
    promoList != null -> {

      listOf(PromoItem.from(promoList.promos[0]))
    }
    else -> {

      val catalogItems = mutableListOf<CatalogItem>()
      catalogItems.add(Header(appsList!!.title, appsList.expandInfo.vitrinExpandInfo.path))

      appsList.apps.mapTo(catalogItems) {
        AppItem(it.info.packageName, it.info.name, it.info.image)
      }
      catalogItems
    }
  }
}
