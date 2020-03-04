package com.asfoundation.wallet.catalog.repository

import com.asfoundation.wallet.entity.BaseRequest
import com.asfoundation.wallet.entity.GetPageV2Reply
import com.asfoundation.wallet.entity.Row
import com.asfoundation.wallet.repository.RequestPropertiesDataSource
import com.asfoundation.wallet.service.GutSingleReply
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject


class CatalogService @Inject constructor(val api: CatalogApi, private val requestPropertiesDataSource: RequestPropertiesDataSource) {

  companion object {
    const val SERVICE_HOST = "catalog_service_host"
  }

  fun getCatalogRows(): Single<List<Row>> {
    return api.getCatalog(getCatalogRequest())
        .map {
          it.page.pageBodyInfo.pageBody.rows
        }
  }

  private fun getCatalogRequest(): BaseRequest = BaseRequest(requestPropertiesDataSource.getRequestProperties())
}

interface CatalogApi {

  @GutSingleReply
  @POST("GetPageV2Request")
  fun getCatalog(@Body catalogRequest: BaseRequest): Single<GetPageV2Reply>
}