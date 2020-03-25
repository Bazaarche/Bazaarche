package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.asf.wallet.R
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.IabView
import com.asfoundation.wallet.ui.setVisible
import com.phelat.poolakey.Payment
import com.phelat.poolakey.rx.connect
import com.phelat.poolakey.rx.onActivityResult
import com.phelat.poolakey.rx.purchaseProduct
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_bazaar_iab.*
import javax.inject.Inject


class BazaarIabFragment : DaggerFragment() {


  companion object {

    const val ARG_TRANSACTION = "transaction"

    private const val TAG = "BazaarIabFragment"

    fun newInstance(transaction: TransactionBuilder): BazaarIabFragment {
      val fragment = BazaarIabFragment()
      val bundle = Bundle()
      bundle.putParcelable(ARG_TRANSACTION, transaction)

      fragment.arguments = bundle
      return fragment
    }

  }

  private lateinit var iabView: IabView

  private lateinit var transactionCompleteAnimationView: LottieAnimationView
  private lateinit var errorMessage: TextView
  private lateinit var errorOkButton: View

  @Inject
  lateinit var bazaarIabViewModelFactory: BazaarIabViewModelFactory

  private lateinit var viewModel: BazaarIabViewModel

  private val payment by lazy(LazyThreadSafetyMode.NONE) {
    Payment(context = requireContext(), config = viewModel.paymentConfiguration)
  }

  private lateinit var paymentConnection: Disposable


  override fun onAttach(context: Context) {
    super.onAttach(context)
    check(context is IabView) { "BazaarIabFragment must be attached to IabActivity" }
    iabView = context
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProviders.of(this, bazaarIabViewModelFactory)[BazaarIabViewModel::class.java]
    observePurchaseState()
    connectPayment()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater.inflate(R.layout.fragment_bazaar_iab, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    errorMessage = view.findViewById(R.id.activity_iab_error_message)
    errorOkButton = view.findViewById(R.id.activity_iab_error_ok_button)
    transactionCompleteAnimationView = view.findViewById(R.id.lottie_transaction_success)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    val purchaseResult = payment.onActivityResult(requestCode, resultCode, data)
    viewModel.onPurchaseFinished(data, purchaseResult)
  }

  override fun onDestroy() {
    paymentConnection.dispose()
    super.onDestroy()
  }

  private fun observePurchaseState() {
    viewModel.purchaseState.observe(this, Observer {

      loadingView.setVisible(it.isLoading)

      when (it) {
        is PurchaseState.Purchased -> {
          onPurchaseFlowFinished(it.purchaseData)
        }


        is PurchaseState.BazaarNotFoundError -> {
          showBazaarInstallDialog()
        }

        is PurchaseState.Error -> {
          showError(it.errorBundle)
        }
      }
    })
  }

  private fun connectPayment() {
    paymentConnection = payment.connect().subscribe({ onConnectionFinished() }, viewModel::onConnectionError)
  }

  private fun onConnectionFinished() {

    viewModel.getPurchaseRequest().observe(this, Observer {
      payment.purchaseProduct(this, it)
          .subscribe()
    })
  }


  private fun onPurchaseFlowFinished(bundle: Bundle) {
    //TODO show something when purchase finished
    iabView.finish(bundle)
  }

  private fun showBazaarInstallDialog() {
    DialogWalletInstall(requireContext()).apply {
      setOnCancelListener { viewModel.onCancelInstallation() }
      show()
    }
  }

  private fun showError(errorBundle: Bundle) {
    //TODO show something when error happened
    iabView.close(errorBundle)
  }

}
