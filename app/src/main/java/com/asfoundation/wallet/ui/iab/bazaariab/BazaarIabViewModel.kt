package com.asfoundation.wallet.ui.iab.bazaariab

import androidx.lifecycle.ViewModel
import com.phelat.poolakey.callback.PurchaseCallback
import com.phelat.poolakey.config.PaymentConfiguration
import com.phelat.poolakey.config.SecurityCheck
import com.phelat.poolakey.entity.PurchaseEntity
import com.phelat.poolakey.request.PurchaseRequest

class BazaarIabViewModel : ViewModel() {

  companion object {

    private const val BASE64_ENCODED_PUBLIC_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDMPD2GdXOVRi13M5glHo0/0hqMPkdhYZ42rYLGCroxOc0W/lZ9zhWtm+zF5Epa98tHeBXmLr9HWJJz2v4HGCaPYHo0up7ogEMbWCLIniN9N6j42Tt/naPZWOCbkeHZ5b7191Zz2cZUi4zAbEJA9uWOf1dS3tS5+7G+lG2yhhtu1dWoAng0wRj2hCoIcy/YENacQRtrRRohp8zWUdPQaK1xwX2mXMuq/Le2c7onjrUCAwEAAQ=="

    private const val PURCHASE_REQUEST = 10001

  }

  private val sku = "test"/*transaction.skuId*///TODO: remove this and use transaction.skuId

  internal val paymentConfiguration = run {
    val securityCheck = SecurityCheck.Enable(rsaPublicKey = BASE64_ENCODED_PUBLIC_KEY)
    PaymentConfiguration(localSecurityCheck = securityCheck)
  }


  internal val purchaseRequest = PurchaseRequest(
      productId = sku,
      requestCode = PURCHASE_REQUEST,
      payload = ""
  )

  fun onPurchaseFinished(purchaseCallback: PurchaseCallback) {

    purchaseCallback.purchaseSucceed {

      if (!verifyDeveloperPayload(it.purchaseInfo.payload)) {
        return@purchaseSucceed
      }

      if (it.purchaseInfo.productId == sku) {

        createTransaction(it)
      }
    }

  }


  private fun verifyDeveloperPayload(payload: String): Boolean {
    /*
     * TODO: verify that the developer payload of the purchase is correct.
     */
    return true
  }


  private fun createTransaction(purchase: PurchaseEntity) {//TODO
//    val disposable = bazaarIabUtils.start(transaction, purchase.developerPayload,
//        purchase.token, isBds, activity.packageName)
//
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe({
//          Log.d(TAG, "status: " + it.status + " ,uid: " + it.uid + " ,data: " + it.data)
//        }, {
//          Log.d(TAG, it.message)
//        })
  }

}