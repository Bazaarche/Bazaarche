package com.asfoundation.wallet.ui.iab.bazaariab

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window
import com.asf.wallet.R
import com.asfoundation.wallet.ui.dp
import kotlinx.android.synthetic.main.dialog_wallet_install.*

class DialogWalletInstall(context: Context) : Dialog(context) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.dialog_wallet_install)
    setCancelable(false)
    buildTop()
    buildMessage()
    buildCancelButton()
    buildDownloadButton()
  }

  private fun buildTop() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      dialogWalletInstallImageGraphic.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
          outline.setRoundRect(0, 0, view.width, view.height + context.dp(12),
              context.dp(12).toFloat())
          view.clipToOutline = true
        }
      }
    }
  }

  private fun buildMessage() {

    val bazaar = context.getString(R.string.bazaar)
    val dialogMessage = context.getString(R.string.app_wallet_install_wallet_from_iab, bazaar)

    val messageStylized = SpannableStringBuilder(dialogMessage)
    val indexOfBazaar = dialogMessage.indexOf(bazaar)
    messageStylized.setSpan(StyleSpan(Typeface.BOLD), indexOfBazaar,
        indexOfBazaar + bazaar.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    dialogWalletInstallTextMessage.text = messageStylized
  }

  private fun buildDownloadButton() {
    dialogWalletInstallButtonDownload.setOnClickListener {
      val browserIntent = buildBrowserIntent()
      if (resolveActivityInfoForIntent(browserIntent)) {
        context.startActivity(browserIntent)
      }
    }
  }

  private fun buildCancelButton() {
    dialogWalletInstallButtonCancel.setOnClickListener {
      cancel()
    }
  }

  private fun resolveActivityInfoForIntent(intent: Intent): Boolean {
    val activityInfo = intent.resolveActivityInfo(context.packageManager, 0)
    return activityInfo != null
  }

  private fun buildBrowserIntent(): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(BAZAAR_INSTALL_URL))
  }

  companion object {
    private const val BAZAAR_INSTALL_URL = "https://cafebazaar.ir/install"
  }
}