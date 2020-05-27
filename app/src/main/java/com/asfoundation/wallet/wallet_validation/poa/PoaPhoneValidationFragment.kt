package com.asfoundation.wallet.wallet_validation.poa

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.asf.wallet.R
import com.asfoundation.wallet.interact.SmsValidationInteract
import com.asfoundation.wallet.wallet_validation.generic.WalletValidationAnalytics
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_phone_validation.*
import javax.inject.Inject


class PoaPhoneValidationFragment : DaggerFragment(),
    PoaPhoneValidationView {

  @Inject
  lateinit var interactor: SmsValidationInteract

  @Inject
  lateinit var analytics: WalletValidationAnalytics

  private var walletValidationView: PoaWalletValidationView? = null
  private lateinit var presenter: PoaPhoneValidationPresenter

  private var countryCode: String? = null
  private var phoneNumber: String? = null
  private var errorMessage: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    presenter =
        PoaPhoneValidationPresenter(this,
            walletValidationView, interactor,
            AndroidSchedulers.mainThread(), Schedulers.io(), CompositeDisposable(), analytics)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_phone_validation, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (arguments?.containsKey(COUNTRY_CODE) == true) {
      countryCode = arguments?.getString(COUNTRY_CODE)
    }
    if (arguments?.containsKey(PHONE_NUMBER) == true) {
      phoneNumber = arguments?.getString(PHONE_NUMBER)
    }
    if (arguments?.containsKey(ERROR_MESSAGE) == true) {
      errorMessage = arguments?.getInt(ERROR_MESSAGE)
    }

    presenter.present()
  }

  override fun onResume() {
    super.onResume()

    presenter.onResume()
    focusAndShowKeyboard(phone_number)
  }

  override fun setupUI() {
    ccp.registerCarrierNumberEditText(phone_number)

    countryCode?.let {
      ccp.setCountryForPhoneCode(it.drop(0).toInt())
    }
    phoneNumber?.let { phone_number.setText(it) }

    errorMessage?.let { setError(it) }

  }

  override fun setError(message: Int) {
    phone_number_layout.error = getString(message)
  }

  override fun clearError() {
    phone_number_layout.error = null
  }

  override fun getCountryCode() = Observable.just(ccp.selectedCountryCodeWithPlus)


  override fun getPhoneNumber(): Observable<String> {
    return RxTextView.afterTextChangeEvents(phone_number)
        .map {
          it.editable()
              ?.toString()
        }
  }

  override fun setButtonState(state: Boolean) {
    submit_button.isEnabled = state
  }

  override fun getSubmitClicks(): Observable<Pair<String, String>> {
    return RxView.clicks(submit_button)
        .map {
          Pair(ccp.selectedCountryCodeWithPlus,
              ccp.fullNumber.substringAfter(ccp.selectedCountryCode))
        }
  }

  override fun getCancelClicks() = RxView.clicks(cancel_button)

  override fun onDestroy() {
    presenter.stop()
    super.onDestroy()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)

    if (context !is PoaWalletValidationView) {
      throw IllegalStateException(
          "PoaPhoneValidationFragment must be attached to Wallet Validation activity")
    }

    walletValidationView = context
  }

  override fun onDetach() {
    super.onDetach()
    walletValidationView = null
  }

  companion object {

    internal const val COUNTRY_CODE = "COUNTRY_CODE"
    internal const val PHONE_NUMBER = "PHONE_NUMBER"
    internal const val ERROR_MESSAGE = "ERROR_MESSAGE"

    @JvmStatic
    fun newInstance(countryCode: String? = null, phoneNumber: String? = null,
                    errorMessage: Int? = null): Fragment {
      val bundle = Bundle().apply {
        putString(COUNTRY_CODE, countryCode)
        putString(PHONE_NUMBER, phoneNumber)
      }

      errorMessage?.let {
        bundle.putInt(ERROR_MESSAGE, errorMessage)
      }

      return PoaPhoneValidationFragment().apply { arguments = bundle }
    }

  }

  private fun focusAndShowKeyboard(view: EditText) {
    view.post {
      view.requestFocus()
      val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
      imm?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }
  }

}
