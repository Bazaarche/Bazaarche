package com.asfoundation.wallet.ui.iab

import android.content.Intent
import android.os.Bundle
import com.asfoundation.wallet.billing.adyen.PaymentType
import java.math.BigDecimal

/**
 * Created by franciscocalado on 20/07/2018.
 */

interface IabView {

  fun disableBack()
  fun enableBack()
  fun finish(bundle: Bundle)
  fun showError()
  fun close(bundle: Bundle?)
  fun navigateToWebViewAuthorization(url: String)
  fun showOnChain(amount: BigDecimal, isBds: Boolean, bonus: String)
  fun showAdyenPayment(amount: BigDecimal, currency: String?, isBds: Boolean,
                       paymentType: PaymentType, bonus: String?, isPreselected: Boolean,
                       iconUrl: String?)

  fun showAppcoinsCreditsPayment(appcAmount: BigDecimal)
  fun showLocalPayment(domain: String, skuId: String?, originalAmount: String?, currency: String?,
                       bonus: String?, selectedPaymentMethod: String, developerAddress: String,
                       type: String, amount: BigDecimal, callbackUrl: String?,
                       orderReference: String?, payload: String?)

  fun showPaymentMethodsView()
  fun showShareLinkPayment(domain: String, skuId: String?, originalAmount: String?,
                           originalCurrency: String?, amount: BigDecimal, type: String,
                           selectedPaymentMethod: String)

  fun showMergedAppcoins(fiatAmount: BigDecimal, currency: String, bonus: String,
                         productName: String?, appcEnabled: Boolean, creditsEnabled: Boolean,
                         isBds: Boolean, isDonation: Boolean)

  fun showWalletBlocked()
  fun lockRotation()
  fun unlockRotation()
  fun showEarnAppcoins(domain: String, skuId: String?, amount: BigDecimal, type: String)
  fun launchIntent(intent: Intent)
  fun showUpdateRequiredView()
}
