package com.asfoundation.wallet.di

import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.bazaariab.BazaarIabFragment
import dagger.Module
import dagger.Provides

@Module
internal class IabModule {

  @Provides
  fun provideTransactionBuilder(fragment: BazaarIabFragment): TransactionBuilder =
      fragment.requireArguments().getParcelable(BazaarIabFragment.ARG_TRANSACTION)!!

}