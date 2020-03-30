package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.asf.wallet.R
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.IabView
import com.phelat.poolakey.Payment
import com.phelat.poolakey.rx.connect
import com.phelat.poolakey.rx.onActivityResult
import com.phelat.poolakey.rx.purchaseProduct
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_bazaar_iab.*
import kotlinx.android.synthetic.main.fragment_iab_transaction_completed.*
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

  private lateinit var errorMessageView: TextView
  private lateinit var errorOkButton: Button

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

    errorMessageView = view.findViewById(R.id.activity_iab_error_message)
    errorOkButton = view.findViewById(R.id.activity_iab_error_ok_button)
    setupTransactionCompleteAnimationView()
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

      loadingView.visibility = View.GONE

      when (it) {

        PurchaseState.InProgress -> {
          onInProgress()
        }

        PurchaseState.Purchased -> {
          onPurchased()
        }

        is PurchaseState.Finished -> {
          onFinish(it.purchaseData)
        }

        PurchaseState.BazaarNotFoundError -> {
          showBazaarInstallDialog()
        }

        is PurchaseState.Canceled -> {
          onCancelled(it.cancelBundle)
        }

        is PurchaseState.Error -> {
          onError(it)
        }
      }
    })
  }

  private fun connectPayment() {
    paymentConnection = payment.connect().subscribe({ onConnectionFinished() }, viewModel::onConnectionError)
  }

  private fun setupTransactionCompleteAnimationView() {
    lottie_transaction_success.apply {
      setAnimation(R.raw.success_animation)
      addLottieOnCompositionLoadedListener {
        viewModel.animationDuration = duration
      }
    }
  }

  private fun onInProgress() {
    loadingView.visibility = View.VISIBLE
  }

  private fun onPurchased() {
    transactionCompletedView.visibility = View.VISIBLE
  }

  private fun onFinish(bundle: Bundle) {
    iabView.finish(bundle)
  }

  private fun showBazaarInstallDialog() {
    DialogWalletInstall(requireContext()).apply {
      setOnCancelListener { viewModel.onCancelInstallation() }
      show()
    }
  }

  private fun onCancelled(cancelBundle: Bundle) {
    close(cancelBundle)
  }

  private fun onError(error: PurchaseState.Error) {
    errorView.visibility = View.VISIBLE
    errorMessageView.setText(error.textRes)
    setOkErrorClickListener(error.errorBundle)
  }

  private fun onConnectionFinished() {

    viewModel.getPurchaseRequest().observe(this, Observer {
      payment.purchaseProduct(this, it)
          .subscribe()
    })
  }

  private fun setOkErrorClickListener(errorBundle: Bundle) {
    errorOkButton.setOnClickListener {
      close(errorBundle)
    }
  }

  private fun close(bundle: Bundle) {
    iabView.close(bundle)
  }

}
