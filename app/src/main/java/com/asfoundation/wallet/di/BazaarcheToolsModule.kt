package com.asfoundation.wallet.di

import com.asfoundation.wallet.repository.PreferencesDataSource
import com.asfoundation.wallet.repository.PreferencesDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
interface BazaarcheToolsModule {

  @Binds
  fun bindsPreferencesDataSource(preferencesDataSource: PreferencesDataSourceImpl): PreferencesDataSource
}