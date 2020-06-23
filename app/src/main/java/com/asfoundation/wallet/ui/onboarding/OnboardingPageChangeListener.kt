package com.asfoundation.wallet.ui.onboarding

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.asf.wallet.R
import com.rd.PageIndicatorView


class OnboardingPageChangeListener internal constructor(private val view: View,
                                                        private var isActive: Boolean = false) :
    ViewPager2.OnPageChangeCallback() {

  companion object {
    var ANIMATION_TRANSITIONS = 3
    var pageCount = 3
  }

  private val imageResources = arrayOf(R.drawable.onboarding_one, R.drawable.onboarding_two,
      R.drawable.onboarding_three)

  private lateinit var onboardingImage: ImageView
  private lateinit var skipButton: Button
  private lateinit var nextButton: Button
  private lateinit var continueButton: Button
  private lateinit var beenInvitedButton: Button
  private lateinit var checkBox: CheckBox
  private lateinit var warningText: TextView
  private lateinit var termsConditionsLayout: LinearLayout
  private lateinit var pageIndicatorView: PageIndicatorView
  private var currentPage = 0

  init {
    init()
  }

  fun init() {
    onboardingImage = view.findViewById(R.id.onboarding_image)
    skipButton = view.findViewById(R.id.skip_button)
    nextButton = view.findViewById(R.id.next_button)
    continueButton = view.findViewById(R.id.continue_button)
    checkBox = view.findViewById(R.id.onboarding_checkbox)
    beenInvitedButton = view.findViewById(R.id.been_invited_bonus)
    warningText = view.findViewById(R.id.terms_conditions_warning)
    termsConditionsLayout = view.findViewById(R.id.terms_conditions_layout)
    pageIndicatorView = view.findViewById(R.id.page_indicator)
    updatePageIndicator(0)
    handleUI(0)
  }

  fun setIsActiveFlag(isActive: Boolean) {
    this.isActive = isActive
  }

  fun updateUI() {
    if (isActive && currentPage == 3) beenInvitedButton.visibility = View.VISIBLE
  }

  private fun animateHideWarning(textView: TextView) {
    val animation = AnimationUtils.loadAnimation(view.context, R.anim.fast_fade_out_animation)
    textView.animation = animation
  }

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    onboardingImage.setImageResource(imageResources[position])
    checkBox.setOnClickListener { handleUI(position) }
    updatePageIndicator(position)
    currentPage = position
    handleUI(position)
  }

  private fun handleUI(position: Int) {
    if (position < 2) {
      showFirstPageLayout()
    } else if (position == 2) {
      showLastPageLayout()
    }
  }

  private fun showLastPageLayout() {
    skipButton.visibility = View.GONE
    termsConditionsLayout.visibility = View.VISIBLE
    nextButton.isEnabled = checkBox.isChecked
    continueButton.isEnabled = checkBox.isChecked
    beenInvitedButton.isEnabled = checkBox.isChecked

    if (checkBox.isChecked) {
      if (warningText.visibility == View.VISIBLE) {
        animateHideWarning(warningText)
        warningText.visibility = View.INVISIBLE
      }
    }
  }

  private fun showFirstPageLayout() {
    nextButton.visibility = View.GONE
    continueButton.isEnabled = true
    beenInvitedButton.visibility = View.GONE
    termsConditionsLayout.visibility = View.GONE

    if (warningText.visibility == View.VISIBLE) {
      animateHideWarning(warningText)
      warningText.visibility = View.INVISIBLE
    }
  }

  private fun updatePageIndicator(position: Int) {
    val pos: Int
    val config = view.resources.configuration
    pos = if (config.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
      position
    } else {
      pageCount - position - 1
    }
    pageIndicatorView.setSelected(pos)
  }

  override fun onPageSelected(position: Int) {

  }

  override fun onPageScrollStateChanged(state: Int) {

  }
}