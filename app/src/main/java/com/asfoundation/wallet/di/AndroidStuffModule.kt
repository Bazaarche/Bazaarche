package com.asfoundation.wallet.di

import com.asfoundation.wallet.ui.balance.BazaarcheTransactionDetailActivity
import com.asfoundation.wallet.ui.bazarchesettings.TransactionsFragment
import com.asfoundation.wallet.ui.bazarchesettings.backuprestore.WalletFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidStuffModule {

  @ContributesAndroidInjector(modules = [TransactionsModule::class])
  abstract fun transactionsFragment(): TransactionsFragment


  @ContributesAndroidInjector(modules = [AccountsManageModule::class])
  abstract fun walletFragment(): WalletFragment

  @ActivityScope
  @ContributesAndroidInjector(modules = [TransactionDetailModule::class])
  abstract fun bindBazaarcheTransactionDetailModule(): BazaarcheTransactionDetailActivity
}