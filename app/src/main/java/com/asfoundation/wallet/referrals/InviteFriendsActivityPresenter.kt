package com.asfoundation.wallet.referrals

import com.asfoundation.wallet.interact.FindDefaultWalletInteract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import java.util.concurrent.TimeUnit

class InviteFriendsActivityPresenter(private val activity: InviteFriendsActivityView,
                                     private val referralInteractor: ReferralInteractorContract,
                                     private val walletInteract: FindDefaultWalletInteract,
                                     private val disposables: CompositeDisposable,
                                     private val networkScheduler: Scheduler,
                                     private val viewScheduler: Scheduler) {

  fun present() {
    handleFragmentNavigation()
    handleRetryClick()
  }

  private fun handleFragmentNavigation() {
    disposables.add(walletInteract.find()
        .flatMap { referralInteractor.retrieveReferral() }
        .subscribeOn(networkScheduler)
        .observeOn(viewScheduler)
        .doOnSuccess { handleValidationResult(it) }
        .flatMapCompletable {
          referralInteractor.saveReferralInformation(it.completed, it.link != null,
              ReferralsScreen.INVITE_FRIENDS)
        }
        .subscribe({}, { handleError(it) })
    )
  }

  private fun handleValidationResult(referral: ReferralModel) {
    if (referral.link != null) {
      activity.navigateToInviteFriends(referral.amount, referral.pendingAmount,
          referral.symbol, referral.link, referral.completed, referral.receivedAmount,
          referral.maxAmount, referral.available, referral.isRedeemed)
      handleInfoButtonVisibility()
    } else {
      activity.navigateToVerificationFragment(referral.amount, referral.symbol)
    }
  }

  private fun handleInfoButtonVisibility() {
    disposables.add(activity.infoButtonInitialized()
        .filter { it }
        .doOnNext { activity.showInfoButton() }
        .subscribe({}, { it.printStackTrace() }))
  }

  private fun handleError(throwable: Throwable) {
    throwable.printStackTrace()
    if (isNoNetworkException(throwable)) {
      activity.showNetworkErrorView()
    }
  }

  private fun isNoNetworkException(throwable: Throwable): Boolean {
    return throwable is IOException || throwable.cause != null && throwable.cause is IOException
  }

  private fun handleRetryClick() {
    disposables.add(activity.retryClick()
        .observeOn(viewScheduler)
        .doOnNext { activity.showRetryAnimation() }
        .delay(1, TimeUnit.SECONDS)
        .doOnNext { handleFragmentNavigation() }
        .subscribe({}, { it.printStackTrace() }))
  }

  fun stop() {
    disposables.clear()
  }
}