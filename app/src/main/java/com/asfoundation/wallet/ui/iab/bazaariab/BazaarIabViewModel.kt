package com.asfoundation.wallet.ui.iab.bazaariab

import androidx.lifecycle.ViewModel
import com.asf.wallet.BuildConfig
import com.phelat.poolakey.callback.PurchaseCallback
import com.phelat.poolakey.config.PaymentConfiguration
import com.phelat.poolakey.config.SecurityCheck
import com.phelat.poolakey.entity.PurchaseEntity
import com.phelat.poolakey.request.PurchaseRequest

class BazaarIabViewModel : ViewModel() {

  companion object {

    private const val PURCHASE_REQUEST = 10001

  }

  private val sku = "test"/*transaction.skuId*///TODO: remove this and use transaction.skuId

  internal val paymentConfiguration = run {
    val securityCheck = SecurityCheck.Enable(rsaPublicKey = BuildConfig.BAZAARCHE_IAB_KEY)
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