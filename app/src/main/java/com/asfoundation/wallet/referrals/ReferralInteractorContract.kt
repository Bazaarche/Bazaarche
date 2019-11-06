package com.asfoundation.wallet.referrals

import com.appcoins.wallet.gamification.repository.entity.ReferralResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface ReferralInteractorContract {

  fun hasReferralUpdate(address: String, friendsInvited: Int, isVerified: Boolean,
                        screen: ReferralsScreen): Single<Boolean>

  fun hasReferralUpdate(screen: ReferralsScreen): Single<Boolean>

  fun retrieveReferral(): Single<ReferralsModel>

  fun saveReferralInformation(numberOfFriends: Int, isVerified: Boolean,
                              screen: ReferralsScreen): Completable

  fun getPendingBonusNotification(): Maybe<ReferralNotification>

  fun getReferralInfo(): Single<ReferralResponse>

  fun getUnwatchedPendingBonusNotification(): Single<CardNotification>

  fun dismissNotification(referralNotification: ReferralNotification): Completable
}
