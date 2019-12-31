package com.asfoundation.wallet.entity

data class CatalogResponse(val singleReply: SingleReply)
data class SingleReply(val getPageByPathReply: GetPageByPathReply)
class GetPageByPathReply(val pages: Array<Page>)
data class Page(val rows: List<Row>)

data class Row(val title: String, val more: String, val hasMore: Boolean,
               val hamiPromo: HamiPromotion?, val appList: AppListResponse?)


data class HamiPromotion(val link: String, val imageURL: String, val app: App)

class AppListResponse(val applist: Array<App>)

data class App(val packageName: String, val name: String, val image: String)
