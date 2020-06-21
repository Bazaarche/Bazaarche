package com.asfoundation.wallet.ui.bazarchesettings.backuprestore

import com.asfoundation.wallet.viewmodel.WalletsViewModel

fun WalletsViewModel.exportWallet(password: String) {

  exportWallet(defaultWallet().value ?: return, password)
}