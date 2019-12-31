package com.asfoundation.wallet.catalog.repository

import com.asfoundation.wallet.entity.CatalogResponse
import com.asfoundation.wallet.entity.Row
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Inject


class CatalogService @Inject constructor(val api: CatalogApi) {

  companion object {
    const val SERVICE_HOST = "catalog_service_host"
  }

  fun getCatalogRows(): Single<List<Row>> {

    return api.getCatalog()
        .map {
          it.singleReply.getPageByPathReply.pages[0].rows
        }
        .map { rows ->
          rows.filter { it.hamiPromo != null || it.appList != null }
        }
  }
}

interface CatalogApi {

  @POST("GetPageByPathRequest")
  @Headers("Authorization: Bearer PbwHrceJ7K2y4HIR93CqF2MEPVBJWPCslMJ1qooI9BChlTfjdncvClS0djrjPEgNMry8auHaZzXThsPexmqbulkDUQfQ8Rf+JVkfyWTId2heiS17eqM7zW7AH612YhrR2lCTm7oCCX2WQGX8iQ==")
  fun getCatalog(@Body body: String = "{\n" +
      "    \"properties\": {\n" +
      "        \"androidClientInfo\": {\n" +
      "            \"adId\": \"28fb2ff9-204f-4cb7-b104-e4072ac63e40\",\n" +
      "            \"adOptOut\": false,\n" +
      "            \"androidId\": \"6097000a6fb6577a\",\n" +
      "            \"city\": \"NA\",\n" +
      "            \"country\": \"NA\",\n" +
      "            \"cpu\": \"x86\",\n" +
      "            \"device\": \"\",\n" +
      "            \"dpi\": 440,\n" +
      "            \"hardware\": \"\",\n" +
      "            \"height\": 2088,\n" +
      "            \"locale\": \"\",\n" +
      "            \"manufacturer\": \"Google\",\n" +
      "            \"mcc\": 310,\n" +
      "            \"mnc\": 260,\n" +
      "            \"model\": \"Android SDK built for x86\",\n" +
      "            \"osBuild\": \"\",\n" +
      "            \"product\": \"sdk_gphone_x86\",\n" +
      "            \"province\": \"NA\",\n" +
      "            \"sdkVersion\": 29,\n" +
      "            \"width\": 1080\n" +
      "        },\n" +
      "        \"clientID\": \"hfCuKMmjSGm7E5xfKYX2fA\",\n" +
      "        \"clientVersion\": \"8.6.8\",\n" +
      "        \"clientVersionCode\": 800608,\n" +
      "        \"isKidsEnabled\": false,\n" +
      "        \"language\": 2\n" +
      "    },\n" +
      "    \"singleRequest\": {\n" +
      "        \"getPageByPathRequest\": {\n" +
      "            \"offset\": 0,\n" +
      "            \"path\": \"home\",\n" +
      "            \"referrers\": []\n" +
      "        }\n" +
      "    }\n" +
      "}"): Single<CatalogResponse>
}