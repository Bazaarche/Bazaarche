package com.asfoundation.wallet.navigator

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.asfoundation.wallet.BAZAAR_APP_VIEW_URL
import com.asfoundation.wallet.BAZAAR_PACKAGE_NAME
import com.asfoundation.wallet.ui.catalog.AppItem
import com.asfoundation.wallet.ui.catalog.Hami
import com.asfoundation.wallet.ui.catalog.Header
import com.asfoundation.wallet.util.BaseLifecycleObserver

interface AppsNavigator {

  fun onHamiClicked(hami: Hami)

  fun onHeaderClicked(header: Header)

  fun onAppClicked(appItem: AppItem)
}

class AppsNavigatorImpl(activity: ComponentActivity) : AppsNavigator, BaseLifecycleObserver(activity.lifecycle) {

  var activity: Activity? = activity

  override fun onHamiClicked(hami: Hami) {
    openBazaar(hami.link)
  }

  override fun onHeaderClicked(header: Header) {
    openBazaar("bazaar://page?slug=" + header.more)
  }

  override fun onAppClicked(appItem: AppItem) {
    openBazaar(BAZAAR_APP_VIEW_URL + appItem.packageName)
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    activity = null
  }

  private fun openBazaar(uriString: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(uriString)

    activity?.let { activity ->
      val packageManager = activity.packageManager
      val appsList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

      if (appsList.isNotEmpty()) {
        if (appsList.any { it.activityInfo.packageName == BAZAAR_PACKAGE_NAME }) {
          intent.setPackage(BAZAAR_PACKAGE_NAME)
        }
        activity.startActivity(intent)
      }

    }
  }

}