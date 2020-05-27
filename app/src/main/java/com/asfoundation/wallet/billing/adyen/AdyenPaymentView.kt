package com.asfoundation.wallet.billing.adyen

import android.net.Uri
import android.os.Bundle
import com.adyen.checkout.base.model.payments.response.Action
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

interface AdyenPaymentView {

  fun getAnimationDuration(): Long

  fun showProduct()

  fun showLoading()

  fun errorDismisses(): Observable<Any>

  fun buyButtonClicked(): Observable<Any>

  fun showNetworkError()

  fun backEvent(): Observable<Any>

  fun close(bundle: Bundle?)

  fun showSuccess()

  fun showGenericError()

  fun getMorePaymentMethodsClicks(): Observable<Any>

  fun showMoreMethods()

  fun hideLoadingAndShowView()

  fun finishCardConfiguration(
      paymentMethod: com.adyen.checkout.base.model.paymentmethods.PaymentMethod, isStored: Boolean,
      forget: Boolean, savedInstance: Bundle?)

  fun retrievePaymentData(): ReplaySubject<AdyenCardWrapper>

  fun showSpecificError(stringRes: Int)

  fun showCvvError()

  fun showProductPrice(amount: String, currencyCode: String)

  fun lockRotation()

  fun setRedirectComponent(action: Action, uid: String)

  fun submitUriResult(uri: Uri)

  fun getPaymentDetails(): Observable<RedirectComponentModel>

  fun forgetCardClick(): Observable<Any>

  fun hideKeyboard()

  fun adyenErrorCancelClicks(): Observable<Any>

  fun adyenErrorBackClicks(): Observable<Any>

  fun getSupportClicks(): Observable<Any>
}
