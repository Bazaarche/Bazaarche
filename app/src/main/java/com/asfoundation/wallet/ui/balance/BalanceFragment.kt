package com.asfoundation.wallet.ui.balance

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.asf.wallet.R
import com.asfoundation.wallet.util.CurrencyFormatUtils
import com.asfoundation.wallet.util.WalletCurrency
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.balance_token_item.view.*
import kotlinx.android.synthetic.main.fragment_balance.*
import javax.inject.Inject
import kotlin.math.abs

class BalanceFragment : DaggerFragment(), BalanceFragmentView {

  @Inject
  lateinit var balanceInteract: BalanceInteract
  @Inject
  lateinit var formatter: CurrencyFormatUtils

  private var activityView: BalanceActivityView? = null
  private lateinit var presenter: BalanceFragmentPresenter

  companion object {
    @JvmStatic
    fun newInstance(): BalanceFragment {
      return BalanceFragment()
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context !is BalanceActivityView) {
      throw IllegalStateException(
          "Express checkout buy fragment must be attached to IAB activity")
    }
    activityView = context
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter = BalanceFragmentPresenter(this, balanceInteract,
        Schedulers.io(),
        AndroidSchedulers.mainThread(), CompositeDisposable(), formatter)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_balance, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activityView?.setupToolbar()
    presenter.present()

    (app_bar as AppBarLayout).addOnOffsetChangedListener(
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
          val percentage = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
          setAlpha(balance_label, percentage)
          setAlpha(balance_value, percentage)
          setAlpha(balance_label_placeholder, percentage)
          setAlpha(balance_value_placeholder, percentage)
        })
  }

  override fun onDestroyView() {
    presenter.stop()
    super.onDestroyView()
  }

  override fun setupUI() {
    balance_value_placeholder.playAnimation()
    balance_label_placeholder.playAnimation()

    appcoins_credits_token.token_icon.setImageResource(R.drawable.ic_appc_c_token)
    appcoins_credits_token.token_name.text = getString(R.string.appc_credits_token_name)
    (appcoins_credits_token.token_balance_placeholder as LottieAnimationView).playAnimation()

    appcoins_token.token_icon.setImageResource(R.drawable.ic_appc_token)
    appcoins_token.token_name.text = getString(R.string.appc_token_name)
    (appcoins_token.token_balance_placeholder as LottieAnimationView).playAnimation()

    ether_token.token_icon.setImageResource(R.drawable.ic_eth_token)
    ether_token.token_name.text = getString(R.string.ethereum_token_name)
    (ether_token.token_balance_placeholder as LottieAnimationView).playAnimation()
  }

  @SuppressLint("SetTextI18n")
  override fun updateTokenValue(tokenBalance: String,
                                fiatBalance: String,
                                tokenCurrency: WalletCurrency,
                                fiatCurrency: String) {
    if (tokenBalance != "-1" && fiatBalance != "-1") {
      when (tokenCurrency) {
        WalletCurrency.CREDITS -> {
          appcoins_credits_token.token_balance_placeholder.visibility = View.GONE
          (appcoins_credits_token.token_balance_placeholder as LottieAnimationView).cancelAnimation()
          appcoins_credits_token.token_balance.text =
              "$tokenBalance ${tokenCurrency.symbol}"
          appcoins_credits_token.token_balance.visibility = View.VISIBLE
          appcoins_credits_token.token_balance_converted.text =
              "$fiatCurrency$fiatBalance"
          appcoins_credits_token.token_balance_converted.visibility = View.VISIBLE
        }
        WalletCurrency.APPCOINS -> {
          appcoins_token.token_balance_placeholder.visibility = View.GONE
          (appcoins_token.token_balance_placeholder as LottieAnimationView).cancelAnimation()
          appcoins_token.token_balance.text =
              "$tokenBalance ${tokenCurrency.symbol}"
          appcoins_token.token_balance.visibility = View.VISIBLE
          appcoins_token.token_balance_converted.text =
              "$fiatCurrency$fiatBalance"
          appcoins_token.token_balance_converted.visibility = View.VISIBLE
        }
        WalletCurrency.ETHEREUM -> {
          ether_token.token_balance_placeholder.visibility = View.GONE
          (ether_token.token_balance_placeholder as LottieAnimationView).cancelAnimation()
          ether_token.token_balance.text =
              "$tokenBalance ${tokenCurrency.symbol}"
          ether_token.token_balance.visibility = View.VISIBLE
          ether_token.token_balance_converted.text =
              "$fiatCurrency$fiatBalance"
          ether_token.token_balance_converted.visibility = View.VISIBLE
        }
        else -> return
      }
    }
  }

  @SuppressLint("SetTextI18n")
  override fun updateOverallBalance(overallBalance: String, currency: String, symbol: String) {
    if (overallBalance != "-1") {
      balance_label_placeholder.visibility = View.GONE
      (balance_label_placeholder as LottieAnimationView).cancelAnimation()
      balance_label.text =
          String.format(getString(R.string.balance_total_body), currency)
      balance_label.visibility = View.VISIBLE
      balance_value_placeholder.visibility = View.GONE
      (balance_value_placeholder as LottieAnimationView).cancelAnimation()
      balance_value.text = symbol + overallBalance
      balance_value.visibility = View.VISIBLE
    }
  }

  override fun getCreditClick(): Observable<View> {
    return RxView.clicks(appcoins_credits_token)
        .map { appcoins_credits_token }
  }

  override fun getAppcClick(): Observable<View> {
    return RxView.clicks(appcoins_token)
        .map { appcoins_token }
  }

  override fun getEthClick(): Observable<View> {
    return RxView.clicks(ether_token)
        .map { ether_token }
  }

  override fun showTokenDetails(view: View) {
    lateinit var tokenId: TokenDetailsActivity.TokenDetailsId
    when (view) {
      appcoins_credits_token -> tokenId = TokenDetailsActivity.TokenDetailsId.APPC_CREDITS
      appcoins_token -> tokenId = TokenDetailsActivity.TokenDetailsId.APPC
      ether_token -> tokenId = TokenDetailsActivity.TokenDetailsId.ETHER
    }

    activityView?.showTokenDetailsScreen(tokenId, view.token_icon, view.token_name, view)
  }

  override fun getTopUpClick(): Observable<Any> {
    return RxView.clicks(top_up_btn)
  }

  override fun showTopUpScreen() {
    activityView?.showTopUpScreen()
  }

  private fun setAlpha(view: View, alphaPercentage: Float) {
    view.alpha = 1 - alphaPercentage * 1.20f
  }
}
