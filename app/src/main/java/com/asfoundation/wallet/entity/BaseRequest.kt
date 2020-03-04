package com.asfoundation.wallet.entity

data class BaseRequest(val properties: RequestProperties) {
  val singleRequest = SingleRequest()
}

class SingleRequest {
  val getPageV2Request = GetPageV2Request()
}

class GetPageV2Request {
  val path: String = "home_wallet"
}