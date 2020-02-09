package com.asfoundation.wallet.di;

import com.asfoundation.wallet.navigator.AppsNavigator;
import com.asfoundation.wallet.navigator.AppsNavigatorImpl;
import com.asfoundation.wallet.ui.catalog.CatalogActivity;
import dagger.Module;
import dagger.Provides;

@Module
class CatalogModule {

  @Provides
  AppsNavigator provideAppNavigator(CatalogActivity activity) {
    return new AppsNavigatorImpl(activity);
  }
}
