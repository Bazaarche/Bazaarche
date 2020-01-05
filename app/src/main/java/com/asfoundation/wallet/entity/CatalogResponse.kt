package com.asfoundation.wallet.entity

data class CatalogResponse(val singleReply: SingleReply)
data class SingleReply(val getPageByPathReply: GetPageByPathReply)
class GetPageByPathReply(val pages: Array<Page>)
data class Page(val rows: List<Row>)

data class Row(val title: String, val more: String, val hasMore: Boolean,
               val hamiPromo: HamiData?, val appList: AppListResponse?)


data class HamiData(val title: String, val shortDescription: String, val link: String,
                    val imageURL: String, val app: App)

class AppListResponse(val appList: Array<App>)

data class App(val packageName: String, val name: String, val image: String)
