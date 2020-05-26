package com.appcoins.wallet.gamification.repository

import com.appcoins.wallet.gamification.repository.entity.GamificationResponse
import com.appcoins.wallet.gamification.repository.entity.ReferralResponse
import com.appcoins.wallet.gamification.repository.entity.UserStatusResponse
import io.reactivex.Completable
import io.reactivex.Single
import java.math.BigDecimal

interface PromotionsRepository {
  fun getUserStats(wallet: String): Single<UserStats>
  fun getLevels(wallet: String): Single<Levels>
  fun getForecastBonus(wallet: String, packageName: String,
                       amount: BigDecimal): Single<ForecastBonus>

  fun getLastShownLevel(wallet: String, screen: String): Single<Int>
  fun shownLevel(wallet: String, level: Int, screen: String): Completable

  fun getUserStatus(wallet: String): Single<UserStatusResponse>
  fun getGamificationUserStatus(wallet: String): Single<GamificationResponse>
  fun getReferralUserStatus(wallet: String): Single<ReferralResponse>
  fun getReferralInfo(): Single<ReferralResponse>
}
