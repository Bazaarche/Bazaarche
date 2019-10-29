package com.asfoundation.wallet.ui


interface SettingsView {

  fun setupPreferences()
  fun setVerifiedWalletPreference()
  fun setUnverifiedWalletPreference()
  fun setWalletValidationNoNetwork()
  fun setWalletsPreference(walletAddress: String)
  fun setRedeemCodePreference(walletAddress: String)
}