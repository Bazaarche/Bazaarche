package com.asfoundation.wallet.ui.catalog

import com.asfoundation.wallet.entity.App
import com.asfoundation.wallet.entity.HamiData
import com.asfoundation.wallet.entity.Promo
import com.asfoundation.wallet.entity.Row

sealed class CatalogItem


data class Hami(val title: String, val shortDescription: String, val link: String,
                val imageURL: String, val app: App) : CatalogItem() {

  companion object {
    fun from(hamiData: HamiData): Hami = hamiData.run {
      Hami(title, shortDescription, link, imageURL, app)
    }
  }
}

data class PromoItem(val title: String, val link: String, val imageURL: String): CatalogItem() {

  companion object {
    fun from(promo: Promo): PromoItem = promo.run {
      PromoItem(title, link, image)
    }
  }
}

data class Header(val title: String, val more: String) : CatalogItem()

data class AppItem(val packageName: String, val name: String, val image: String) : CatalogItem()

fun Row.toCatalogItems(): List<CatalogItem> {

  return when {
    hamiPromo != null -> {

      listOf(Hami.from(hamiPromo))
    }
    promoList != null -> {

      listOf(PromoItem.from(promoList.promoList[0]))
    }
    else -> {

      val catalogItems = mutableListOf<CatalogItem>()
      catalogItems.add(Header(title, more))

      appList?.appList?.mapTo(catalogItems) {
        AppItem(it.packageName, it.name, it.image)
      }
      catalogItems
    }
  }
}
