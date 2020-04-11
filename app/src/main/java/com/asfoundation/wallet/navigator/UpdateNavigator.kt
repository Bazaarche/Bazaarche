package com.asfoundation.wallet.navigator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.asfoundation.wallet.BAZAAR_PACKAGE_NAME

class UpdateNavigator {

  fun navigateToStoreAppView(context: Context?, url: String) {
    context?.let {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      val packageManager = context.packageManager
      val appsList =
          packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
      appsList?.let {
        for (info in appsList) {
          if (info.activityInfo.packageName == BAZAAR_PACKAGE_NAME) {
            intent.setPackage(info.activityInfo.packageName)
            break
          }
        }
        if (appsList.isNotEmpty()) {
          context.startActivity(intent)
        }
      }
    }
  }
}