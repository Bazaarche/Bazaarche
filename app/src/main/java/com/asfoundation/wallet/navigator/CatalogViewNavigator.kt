package com.asfoundation.wallet.navigator

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.asfoundation.wallet.BAZAAR_APP_VIEW_URL
import com.asfoundation.wallet.BAZAAR_PACKAGE_NAME
import com.asfoundation.wallet.ui.bazarchesettings.BazaarcheSettingsActivity
import com.asfoundation.wallet.ui.catalog.AppItem
import com.asfoundation.wallet.ui.catalog.CatalogAdapter
import com.asfoundation.wallet.ui.catalog.Hami
import com.asfoundation.wallet.ui.catalog.Header
import javax.inject.Inject

class CatalogViewNavigator @Inject constructor(activity: Activity) : CatalogAdapter.OnCatalogItemClicked {

  var activity: Activity? = activity

  fun openSettings() {
    activity?.let {
      val intent = Intent(it, BazaarcheSettingsActivity::class.java)
      it.startActivity(intent)
    }
  }

  override fun onHamiClicked(hami: Hami) {
    openBazaar(hami.link)
  }

  override fun onHeaderClicked(header: Header) {
    openBazaar("bazaar://page?slug=" + header.more)
  }

  override fun onAppClicked(appItem: AppItem) {
    openBazaar(BAZAAR_APP_VIEW_URL + appItem.packageName)
  }

  fun destroy() {
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
