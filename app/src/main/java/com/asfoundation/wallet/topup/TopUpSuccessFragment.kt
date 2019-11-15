package com.asfoundation.wallet.topup

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.FontAssetDelegate
import com.airbnb.lottie.TextDelegate
import com.asf.wallet.R
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_top_up_success.*
import java.math.BigDecimal
import java.math.RoundingMode

class TopUpSuccessFragment : DaggerFragment(), TopUpSuccessFragmentView {

  companion object {
    @JvmStatic
    fun newInstance(amount: String, currency: String, bonus: String): TopUpSuccessFragment {
      val fragment = TopUpSuccessFragment()
      val bundle = Bundle()
      bundle.putString(PARAM_AMOUNT, amount)
      bundle.putString(CURRENCY, currency)
      bundle.putString(BONUS, bonus)
      fragment.arguments = bundle
      return fragment
    }

    private const val PARAM_AMOUNT = "amount"
    private const val CURRENCY = "currency"
    private const val BONUS = "bonus"
  }

  private lateinit var presenter: TopUpSuccessPresenter

  private lateinit var topUpActivityView: TopUpActivityView
  val amount: String? by lazy {
    if (arguments!!.containsKey(PARAM_AMOUNT)) {
      arguments!!.getString(PARAM_AMOUNT)
    } else {
      throw IllegalArgumentException("product name not found")
    }
  }

  val currency: String? by lazy {
    if (arguments!!.containsKey(CURRENCY)) {
      arguments!!.getString(CURRENCY)
    } else {
      throw IllegalArgumentException("currency not found")
    }
  }

  val bonus: String by lazy {
    if (arguments!!.containsKey(BONUS)) {
      arguments!!.getString(BONUS)
    } else {
      throw IllegalArgumentException("bonus not found")
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context !is TopUpActivityView) {
      throw IllegalStateException(
          "Express checkout buy fragment must be attached to IAB activity")
    }
    topUpActivityView = context
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter = TopUpSuccessPresenter(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_top_up_success, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    presenter.present()
    topUpActivityView.showToolbar()
  }

  override fun onDestroyView() {
    presenter.stop()
    super.onDestroyView()
  }

  override fun show() {
    if (bonus.isNotBlank()) {
      top_up_success_animation.setAnimation(R.raw.top_up_bonus_success_animation)
      setAnimationText()
      formatBonusSuccessMessage()
    } else {
      top_up_success_animation.setAnimation(R.raw.top_up_success_animation)
      formatSuccessMessage()
    }
    top_up_success_animation.playAnimation()
  }

  override fun clean() {
    top_up_success_animation.removeAllAnimatorListeners()
    top_up_success_animation.removeAllUpdateListeners()
    top_up_success_animation.removeAllLottieOnCompositionLoadedListener()
  }

  override fun close() {
    topUpActivityView.close(true)
  }


  override fun getOKClicks(): Observable<Any> {
    return RxView.clicks(button)
  }

  private fun setAnimationText() {
    val textDelegate = TextDelegate(top_up_success_animation)
    textDelegate.setText("bonus_value", bonus)
    textDelegate.setText("bonus_received",
        resources.getString(R.string.gamification_purchase_completed_bonus_received))
    top_up_success_animation.setTextDelegate(textDelegate)
    top_up_success_animation.setFontAssetDelegate(object : FontAssetDelegate() {
      override fun fetchFont(fontFamily: String?): Typeface {
        return Typeface.create("sans-serif-medium", Typeface.BOLD)
      }
    })
  }

  private fun formatBonusSuccessMessage() {
    val formattedInitialString = getFormattedTopUpValue()
    val topUpString =
        formattedInitialString + " " + resources.getString(R.string.topup_completed_2_with_bonus)
    setSpannableString(topUpString, formattedInitialString.length)

  }

  private fun formatSuccessMessage() {
    val formattedInitialString = getFormattedTopUpValue()
    val secondStringFormat =
        String.format(resources.getString(R.string.askafriend_notification_received_body),
            formattedInitialString, "\n")
    setSpannableString(secondStringFormat, formattedInitialString.length)
  }

  private fun getFormattedTopUpValue(): String {
    val fiatValue =
        BigDecimal(amount).setScale(2, RoundingMode.FLOOR).toString() + " " + currency
    return String.format(resources.getString(R.string.topup_completed_1), fiatValue)
  }

  private fun setSpannableString(secondStringFormat: String, firstStringLength: Int) {
    val boldStyle = StyleSpan(Typeface.BOLD)
    val sb = SpannableString(secondStringFormat)
    sb.setSpan(boldStyle, 0, firstStringLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    value.text = sb
  }
}
