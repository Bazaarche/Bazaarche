package com.appcoins.wallet.gamification.repository

import io.reactivex.Completable
import io.reactivex.Single

interface GamificationLocalData {
  /**
   * @return -1 if never showed any level
   */
  fun getLastShownLevel(wallet: String, screen: String): Single<Int>

  fun saveShownLevel(wallet: String, level: Int, screen: String): Completable

  fun setGamificationLevel(gamificationLevel: Int): Completable
}
