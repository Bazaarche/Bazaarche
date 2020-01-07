package com.asfoundation.wallet.di;

import com.asfoundation.wallet.navigator.CatalogViewNavigator;
import com.asfoundation.wallet.ui.catalog.CatalogActivity;

import dagger.Module;
import dagger.Provides;

@Module
class CatalogModule {

  @Provides
  CatalogViewNavigator provideCatalogViewNavigator(CatalogActivity activity) {
    return new CatalogViewNavigator(activity);
  }
}
