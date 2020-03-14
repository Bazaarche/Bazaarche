package com.asfoundation.wallet.interact

import android.util.Pair
import com.appcoins.wallet.gamification.GamificationScreen
import com.appcoins.wallet.gamification.repository.Levels
import com.asfoundation.wallet.entity.Balance
import com.asfoundation.wallet.entity.NetworkInfo
import com.asfoundation.wallet.entity.Wallet
import com.asfoundation.wallet.promotions.PromotionsInteractorContract
import com.asfoundation.wallet.referrals.CardNotification
import com.asfoundation.wallet.referrals.ReferralsScreen
import com.asfoundation.wallet.transactions.Transaction
import com.asfoundation.wallet.ui.balance.BalanceInteract
import com.asfoundation.wallet.ui.gamification.GamificationInteractor
import com.asfoundation.wallet.ui.iab.FiatValue
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class TransactionViewInteract(private val findDefaultNetworkInteract: FindDefaultNetworkInteract,
                              private val findDefaultWalletInteract: FindDefaultWalletInteract,
                              private val fetchTransactionsInteract: FetchTransactionsInteract,
                              private val gamificationInteractor: GamificationInteractor,
                              private val balanceInteract: BalanceInteract,
                              private val promotionsInteractor: PromotionsInteractorContract,
                              private val cardNotificationsInteractor: CardNotificationsInteractor,
                              private val autoUpdateInteract: AutoUpdateInteract) {

  val levels: Single<Levels>
    get() = gamificationInteractor.getLevels()

  val appcBalance: Observable<Pair<Balance, FiatValue>>
    get() = balanceInteract.getAppcBalance()

  val ethereumBalance: Observable<Pair<Balance, FiatValue>>
    get() = balanceInteract.getEthBalance()

  val creditsBalance: Observable<Pair<Balance, FiatValue>>
    get() = balanceInteract.getCreditsBalance()

  val cardNotifications: Single<List<CardNotification>>
    get() = cardNotificationsInteractor.getCardNotifications()

  val userLevel: Single<Int>
    get() = gamificationInteractor.getUserStats().map { it.level }

  fun findNetwork(): Single<NetworkInfo> {
    return findDefaultNetworkInteract.find()
  }

  fun hasPromotionUpdate(): Single<Boolean> {
    return promotionsInteractor.hasAnyPromotionUpdate(ReferralsScreen.PROMOTIONS,
        GamificationScreen.PROMOTIONS)
  }

  fun fetchTransactions(wallet: Wallet?): Observable<List<Transaction>> {
    return wallet?.let { fetchTransactionsInteract.fetch(wallet.address) } ?: Observable.just(
        emptyList())
  }

  fun stopTransactionFetch() {
    fetchTransactionsInteract.stop()
  }

  fun findWallet(): Single<Wallet> {
    return findDefaultWalletInteract.find()
  }

  fun dismissNotification(cardNotification: CardNotification): Completable {
    return cardNotificationsInteractor.dismissNotification(cardNotification)
  }

  fun retrieveUpdateUrl(): String {
    return autoUpdateInteract.retrieveRedirectUrl()
  }
}
