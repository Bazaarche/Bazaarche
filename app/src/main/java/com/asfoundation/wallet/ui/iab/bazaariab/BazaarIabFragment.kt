package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.asfoundation.wallet.entity.TransactionBuilder
import com.phelat.poolakey.Connection
import com.phelat.poolakey.Payment
import com.phelat.poolakey.callback.ConnectionCallback
import dagger.android.support.DaggerFragment


class BazaarIabFragment : DaggerFragment() {


  companion object {

    private const val ARG_TRANSACTION = "transaction"

    private const val TAG = "BazaarIabFragment"

    fun newInstance(transaction: TransactionBuilder): BazaarIabFragment {
      val fragment = BazaarIabFragment()
      val bundle = Bundle()
      bundle.putParcelable(ARG_TRANSACTION, transaction)

      fragment.arguments = bundle
      return fragment
    }

  }

  private val transaction by lazy(LazyThreadSafetyMode.NONE) {
    arguments!!.getParcelable<TransactionBuilder>(ARG_TRANSACTION)!!
  }

  private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProviders.of(this)[BazaarIabViewModel::class.java]
  }

  private val payment by lazy(LazyThreadSafetyMode.NONE) {
    Payment(context = requireContext(), config = viewModel.paymentConfiguration)
  }

  private lateinit var paymentConnection: Connection


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    connectPayment()
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    payment.onActivityResult(requestCode, resultCode, data, viewModel::onPurchaseFinished)
  }

  override fun onDestroy() {
    paymentConnection.disconnect()
    super.onDestroy()
  }

  private fun connectPayment() {
    paymentConnection = payment.connect(::onConnectionFinished)
  }


  private fun onConnectionFinished(connectionCallback: ConnectionCallback) {

    connectionCallback.connectionSucceed {
      payment.purchaseProduct(fragment = this, request = viewModel.purchaseRequest) {
        failedToBeginFlow {
          Log.w(TAG, "Payment failedToBeginFlow.")
        }
      }
    }

  }
}
