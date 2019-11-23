package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabHelper
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabResult
import com.asfoundation.wallet.ui.iab.bazaariab.util.Purchase
import dagger.android.support.DaggerFragment
import java.util.*


class BazaarIabFragment : DaggerFragment() {


  companion object {

    private const val ARG_IS_BDS_KEY = "is_bds"
    private const val ARG_TRANSACTION = "transaction"

    private const val TAG = "BazaarIabFragment"

    private const val BASE64_ENCODED_PUBLIC_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDMPD2GdXOVRi13M5glHo0/0hqMPkdhYZ42rYLGCroxOc0W/lZ9zhWtm+zF5Epa98tHeBXmLr9HWJJz2v4HGCaPYHo0up7ogEMbWCLIniN9N6j42Tt/naPZWOCbkeHZ5b7191Zz2cZUi4zAbEJA9uWOf1dS3tS5+7G+lG2yhhtu1dWoAng0wRj2hCoIcy/YENacQRtrRRohp8zWUdPQaK1xwX2mXMuq/Le2c7onjrUCAwEAAQ=="

    private const val PURCHASE_REQUEST = 10001

    private const val GATEWAY = "cafe_bazaar"

    fun newInstance(transaction: TransactionBuilder, isBds: Boolean): BazaarIabFragment {
      val fragment = BazaarIabFragment()
      val bundle = Bundle()
      bundle.putParcelable(ARG_TRANSACTION, transaction)
      bundle.putBoolean(ARG_IS_BDS_KEY, isBds)

      fragment.arguments = bundle
      return fragment
    }

  }

  private var mHelper: IabHelper? = null

  private val transaction by lazy {
    arguments!!.getParcelable<TransactionBuilder>(ARG_TRANSACTION)!!
  }

  private val isBds by lazy {
    arguments!!.getBoolean(ARG_TRANSACTION)
  }

  private val sku = "test"/*transaction.skuId*///TODO: remove this and use transaction.skuId

  private var mPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
    if (mHelper != null) onPurchaseFinished(result, purchase)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mHelper = IabHelper(requireContext(), BASE64_ENCODED_PUBLIC_KEY)
        .apply {
          enableDebugLogging(true)
          startSetup()
        }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    mHelper?.handleActivityResult(requestCode, resultCode, data)
  }

  override fun onDestroy() {
    super.onDestroy()

    mHelper?.dispose()
    mHelper = null
  }

  private fun onPurchaseFinished(result: IabResult, purchase: Purchase?) {

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
     * TODO: verify that the developer payload of the purchase is correct. It will be
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

  private fun IabHelper.startSetup() {
    startSetup(object : IabHelper.OnIabSetupFinishedListener {
      override fun onIabSetupFinished(result: IabResult) {
        Log.d(TAG, "Setup finished.")

        if (mHelper == null) return

        if (!result.isSuccess) {
          return
        }

        mHelper?.launchPurchaseFlow(this@BazaarIabFragment, sku, mapToBazaarItemType(transaction.type), PURCHASE_REQUEST,
            mPurchaseFinishedListener, transaction.payload)
      }

      private fun mapToBazaarItemType(apptoideItemType: String) = apptoideItemType.toLowerCase(Locale.US)
    })
  }
}
