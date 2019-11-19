package com.asfoundation.wallet.ui.iab.bazaariab

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.appcoins.wallet.bdsbilling.repository.RemoteRepository
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabHelper
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabResult
import com.asfoundation.wallet.ui.iab.bazaariab.util.Purchase
import java.util.*


class BazaarIab(private val transaction: TransactionBuilder, activity: Activity) {


  private var mActivity: Activity? = activity

  private var mHelper: IabHelper? = null


  private val sku = "test"/*transaction.skuId*/

  private val isDisposed: Boolean
    get() = mHelper == null || mActivity == null

  private var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = object : IabHelper.OnIabPurchaseFinishedListener {
    override fun onIabPurchaseFinished(result: IabResult, purchase: Purchase?) {
      Log.d(TAG, "Purchase finished: $result, purchase: $purchase")

      if (isDisposed) return

      if (result.isFailure) {
        complain("Error purchasing: $result")
        return
      }
      if (!verifyDeveloperPayload(purchase!!)) {
        complain("Error purchasing. Authenticity verification failed.")
        return
      }

      Log.d(TAG, "Purchase successful.")
      if (purchase.sku == sku) {
        // bought the premium upgrade!
        Log.d(TAG, "Purchase is premium upgrade. Congratulating user.")
      }
    }
  }

  init {

    mHelper = IabHelper(activity, base64EncodedPublicKey)
        .apply {
          enableDebugLogging(true)
          startSetup(object : IabHelper.OnIabSetupFinishedListener {
            override fun onIabSetupFinished(result: IabResult) {
              Log.d(TAG, "Setup finished.")

              if (!result.isSuccess) {
                complain("Problem setting up in-app billing: $result")
                return
              }


              Log.d(TAG, "Setup successful. Querying inventory.")

              if (isDisposed) return

              mHelper!!.launchPurchaseFlow(mActivity, sku, transaction.type.toLowerCase(Locale.US), RC_REQUEST,
                  mPurchaseFinishedListener, transaction.payload)
            }
          })
        }

  }


  //TODO: change to a lifecycle-aware component
  fun dispose() {

    Log.d(TAG, "Destroying helper.")
    mHelper!!.dispose()
    mHelper = null
    mActivity = null
  }

  fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean? {

    return mHelper?.handleActivityResult(requestCode, resultCode, data)
  }

  private fun verifyDeveloperPayload(p: Purchase): Boolean {
    val payload = p.developerPayload

    /*
     * TODO: verify that the developer payload of the purchase is correct. It will be
     * the same one that you sent when initiating the purchase.
     *
     * WARNING: Locally generating a random string when starting a purchase and
     * verifying it here might seem like a good approach, but this will fail in the
     * case where the user purchases an item on one device and then uses your app on
     * a different device, because on the other device you will not have access to the
     * random string you originally generated.
     *
     * So a good developer payload has these characteristics:
     *
     * 1. If two different users purchase an item, the payload is different between them,
     *    so that one user's purchase can't be replayed to another user.
     *
     * 2. The payload must be such that you can verify it even when the app wasn't the
     *    one who initiated the purchase flow (so that items purchased by the user on
     *    one device work on other devices owned by the user).
     *
     * Using your own server to store and verify developer payloads across app
     * installations is recommended.
     */

    return true
  }


  private fun complain(message: String) {
    Log.e(TAG, "**** TrivialDrive Error: $message")
  }

  companion object {

    fun startBazaarIab(transaction: TransactionBuilder, activity: Activity): BazaarIab {

      return BazaarIab(transaction, activity)
    }

    private val TAG = this::class.java.simpleName

    private const val base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDMPD2GdXOVRi13M5glHo0/0hqMPkdhYZ42rYLGCroxOc0W/lZ9zhWtm+zF5Epa98tHeBXmLr9HWJJz2v4HGCaPYHo0up7ogEMbWCLIniN9N6j42Tt/naPZWOCbkeHZ5b7191Zz2cZUi4zAbEJA9uWOf1dS3tS5+7G+lG2yhhtu1dWoAng0wRj2hCoIcy/YENacQRtrRRohp8zWUdPQaK1xwX2mXMuq/Le2c7onjrUCAwEAAQ=="

    private val RC_REQUEST = 10001

    private const val payload = ""

    private const val GATEWAY = "cafe_bazaar"
  }

}
