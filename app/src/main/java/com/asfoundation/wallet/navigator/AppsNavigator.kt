package com.asfoundation.wallet.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.asfoundation.wallet.BAZAAR_APP_DETAILS_DEEP_LINK
import com.asfoundation.wallet.BAZAAR_APP_DETAILS_URL
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
    activity?.let { activity ->

      val uriString = getOpenAppDetailsUri(context = activity, appItem = appItem)

      openBazaar(uriString)
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    activity = null
  }

  private fun openBazaar(uriString: String) {
    val intent = Intent(Intent.ACTION_VIEW)
        .setData(Uri.parse(uriString))

    activity?.let { activity ->
      val packageManager = activity.packageManager

      val canOpenIntent = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
      if (canOpenIntent) {
        activity.startActivity(intent)
      }
    }
  }

  private fun getOpenAppDetailsUri(context: Context, appItem: AppItem): String {
    return if (isBazaarInstalled(context)) {
      BAZAAR_APP_DETAILS_DEEP_LINK + appItem.packageName
    } else {
      BAZAAR_APP_DETAILS_URL + appItem.packageName
    }
  }

  private fun isBazaarInstalled(context: Context): Boolean {

    val packageManager = context.packageManager

    return runCatching { packageManager.getPackageInfo(BAZAAR_PACKAGE_NAME, 0) }.isSuccess
  }

}