package com.asfoundation.wallet.ui.iab.bazaariab

import androidx.lifecycle.ViewModel
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabResult
import com.asfoundation.wallet.ui.iab.bazaariab.util.Purchase
import java.util.*

class BazaarIabViewModel : ViewModel() {


  internal val sku = "test"/*transaction.skuId*///TODO: remove this and use transaction.skuId

  fun onPurchaseFinished(result: IabResult, purchase: Purchase?) {

    if (result.isFailure) {
      return
    } else purchase!!

    if (!verifyDeveloperPayload(purchase)) {
      return
    }

    if (purchase.sku == sku) {

      createTransaction(purchase)
    }
  }


  private fun verifyDeveloperPayload(purchase: Purchase): Boolean {
    val payload = purchase.developerPayload
    /*
     * TODO: verify that the developer payload of the purchase is correct.
     */
    return true
  }


  private fun createTransaction(purchase: Purchase) {//TODO
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

  internal fun mapToBazaarItemType(apptoideItemType: String) = apptoideItemType.toLowerCase(Locale.US)
}