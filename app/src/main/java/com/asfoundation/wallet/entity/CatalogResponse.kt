package com.asfoundation.wallet.entity

class GetPageByPathReply(val pages: Array<Page>)
data class Page(val rows: List<Row>)

data class Row(val title: String, val more: String, val hasMore: Boolean,
               val hamiPromo: HamiData?, val promoList: PromoListResponse?, val appList: AppListResponse?)


data class HamiData(val title: String, val shortDescription: String, val link: String,
                    val imageURL: String, val app: App)

class PromoListResponse(val promoList: Array<Promo>)

class AppListResponse(val appList: Array<App>)

data class Promo(val title: String, val link: String, val image: String)

data class App(val packageName: String, val name: String, val image: String)
