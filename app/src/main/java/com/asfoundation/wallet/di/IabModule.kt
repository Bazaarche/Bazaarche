package com.asfoundation.wallet.di

import com.appcoins.wallet.bdsbilling.Billing
import com.appcoins.wallet.bdsbilling.WalletService
import com.appcoins.wallet.billing.BillingMessagesMapper
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.bazaariab.BazaarIabFragment
import com.asfoundation.wallet.ui.iab.bazaariab.BazaarIabInteract
import com.asfoundation.wallet.ui.iab.bazaariab.BazaarIabViewModelFactory
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers

@Module
internal class IabModule {

  @Provides
  fun provideTransactionBuilder(fragment: BazaarIabFragment): TransactionBuilder =
      fragment.requireArguments().getParcelable(BazaarIabFragment.ARG_TRANSACTION)!!

  @Provides
  fun provideBazaarIabInteract(transaction: TransactionBuilder,
                               walletService: WalletService,
                               billing: Billing,
                               billingMessagesMapper: BillingMessagesMapper,
                               gson: Gson): BazaarIabInteract {

    return BazaarIabInteract(transaction, walletService, billing, billingMessagesMapper, gson, Schedulers.io())
  }

  @Provides
  fun provideBazaarIabViewModelFactory(transaction: TransactionBuilder,
                                       bazaarIabInteract: BazaarIabInteract): BazaarIabViewModelFactory {

    return BazaarIabViewModelFactory(transaction, bazaarIabInteract, Schedulers.io())
  }
}