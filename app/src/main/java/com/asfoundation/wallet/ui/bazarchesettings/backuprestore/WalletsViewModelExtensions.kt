package com.asfoundation.wallet.ui.bazarchesettings.backuprestore

import android.content.Intent
import com.asfoundation.wallet.viewmodel.WalletsViewModel

fun WalletsViewModel.exportWallet(password: String) {

  exportWallet(defaultWallet().value ?: return, password)
}

fun WalletsViewModel.getShareIntent(walletData: String): Intent {

    return Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Keystore")
        putExtra(Intent.EXTRA_TEXT, walletData)
    }
}
