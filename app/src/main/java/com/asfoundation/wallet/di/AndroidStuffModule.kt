package com.asfoundation.wallet.di

import com.asfoundation.wallet.ui.bazarchesettings.TransactionsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidStuffModule {

  @ContributesAndroidInjector(modules = [TransactionsModule::class])
  abstract fun transactionsFragment(): TransactionsFragment
}