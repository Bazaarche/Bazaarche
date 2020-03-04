package com.asfoundation.wallet.entity

import com.google.gson.annotations.SerializedName

class GetPageV2Reply(val page: Page)
data class Page(val pageBodyInfo: PageBodyInfo)
data class PageBodyInfo(val pageBody: PageBody)
data class PageBody(val rows: List<Row>)

data class Row(val hamiItem: HamiData?, val promoList: PromoListResponse?,
               @SerializedName("simpleAppList") val appsList: AppListResponse?)


data class HamiData(val title: String, val shortDescription: String, val link: String,
                    val imageURL: String, val appInfo: AppInfo)

class PromoListResponse(val title: String, val promos: Array<Promo>) {

  data class PromoInfo(val title: String, val link: String, val image: String)
  data class Promo(val info: PromoInfo)
}

class AppListResponse(val title: String, val expandInfo: ExpandInfo, val apps: Array<App>) {

  data class ExpandInfo(val vitrinExpandInfo: VitrinExpandInfo)
  data class VitrinExpandInfo(val path: String)

  data class App(val info: AppInfo)
}

data class AppInfo(val packageName: String, val name: String, val image: String)
