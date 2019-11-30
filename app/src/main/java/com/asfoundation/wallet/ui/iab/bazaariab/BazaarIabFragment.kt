package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabHelper
import com.asfoundation.wallet.ui.iab.bazaariab.util.IabResult
import com.asfoundation.wallet.ui.iab.bazaariab.util.Purchase
import dagger.android.support.DaggerFragment


class BazaarIabFragment : DaggerFragment() {


  companion object {

    private const val ARG_IS_BDS_KEY = "is_bds"
    private const val ARG_TRANSACTION = "transaction"

    private const val TAG = "BazaarIabFragment"

    private const val BASE64_ENCODED_PUBLIC_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDMPD2GdXOVRi13M5glHo0/0hqMPkdhYZ42rYLGCroxOc0W/lZ9zhWtm+zF5Epa98tHeBXmLr9HWJJz2v4HGCaPYHo0up7ogEMbWCLIniN9N6j42Tt/naPZWOCbkeHZ5b7191Zz2cZUi4zAbEJA9uWOf1dS3tS5+7G+lG2yhhtu1dWoAng0wRj2hCoIcy/YENacQRtrRRohp8zWUdPQaK1xwX2mXMuq/Le2c7onjrUCAwEAAQ=="

    private const val PURCHASE_REQUEST = 10001

    fun newInstance(transaction: TransactionBuilder, isBds: Boolean): BazaarIabFragment {
      val fragment = BazaarIabFragment()
      val bundle = Bundle()
      bundle.putParcelable(ARG_TRANSACTION, transaction)
      bundle.putBoolean(ARG_IS_BDS_KEY, isBds)

      fragment.arguments = bundle
      return fragment
    }

  }

  private val iabHelper by lazy(LazyThreadSafetyMode.NONE) {
    IabHelper(requireContext(), BASE64_ENCODED_PUBLIC_KEY)
        .apply {
          enableDebugLogging(true)
        }
  }

  private val transaction by lazy(LazyThreadSafetyMode.NONE) {
    arguments!!.getParcelable<TransactionBuilder>(ARG_TRANSACTION)!!
  }

  private val isBds by lazy(LazyThreadSafetyMode.NONE) {
    arguments!!.getBoolean(ARG_IS_BDS_KEY)
  }

  private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProviders.of(this)[BazaarIabViewModel::class.java]
  }

  private fun onPurchaseFinished(result: IabResult, purchase: Purchase) {
    if (!iabHelper.disposed) {
      viewModel.onPurchaseFinished(result, purchase)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    iabHelper.startSetup()
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    iabHelper.handleActivityResult(requestCode, resultCode, data)
  }

  override fun onDestroy() {
    super.onDestroy()

    iabHelper.dispose()
  }

  private fun IabHelper.startSetup() {
    startSetup(object : IabHelper.OnIabSetupFinishedListener {
      override fun onIabSetupFinished(result: IabResult) {
        Log.d(TAG, "Setup finished.")

        if (disposed || result.isFailure) {
          return
        }

        launchPurchaseFlow(this@BazaarIabFragment,
            viewModel.sku,
            viewModel.mapToBazaarItemType(transaction.type),
            PURCHASE_REQUEST,
            ::onPurchaseFinished,
            transaction.payload)
      }

    })
  }
}
