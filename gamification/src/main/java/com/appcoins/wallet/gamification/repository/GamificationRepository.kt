package com.appcoins.wallet.gamification.repository

import io.reactivex.Single
import java.math.BigDecimal

interface GamificationRepository {
  fun getUserStatus(wallet: String): Single<UserStats>
  fun getLevels(): Single<Levels>
  fun getForecastBonus(wallet: String, packageName: String,
                       amount: BigDecimal): Single<ForecastBonus>
}