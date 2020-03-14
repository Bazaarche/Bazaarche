package com.appcoins.wallet.billing.adyen

import com.adyen.checkout.base.model.payments.response.RedirectAction
import com.appcoins.wallet.billing.util.Error

data class PaymentModel(val resultCode: String, val refusalReason: String?, val refusalCode: Int?,
                        val action: RedirectAction?, val redirectUrl: String?,
                        val paymentData: String?, val uid: String,
                        val hash: String?, val orderReference: String?,
                        val status: TransactionResponse.Status, val error: Error = Error()) {
  constructor(error: Error) : this("", null, null, null, "", "", "", null, "",
      TransactionResponse.Status.FAILED, error)
}
