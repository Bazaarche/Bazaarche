package com.asfoundation.wallet.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import io.reactivex.Completable
import io.reactivex.Single

class SharedPreferencesRepository(context: Context) : PreferencesRepositoryType {

  private val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

  override fun hasCompletedOnboarding() = pref.getBoolean(ONBOARDING_COMPLETE_KEY, false)

  override fun setOnboardingComplete() {
    pref.edit()
        .putBoolean(ONBOARDING_COMPLETE_KEY, true)
        .apply()
  }

  override fun hasClickedSkipOnboarding() = pref.getBoolean(ONBOARDING_SKIP_CLICKED_KEY, false)

  override fun setOnboardingSkipClicked() {
    pref.edit()
        .putBoolean(ONBOARDING_SKIP_CLICKED_KEY, true)
        .apply()
  }

  override fun getCurrentWalletAddress(): String? {
    return pref.getString(CURRENT_ACCOUNT_ADDRESS_KEY, null)
  }

  override fun setCurrentWalletAddress(address: String) {
    pref.edit()
        .putString(CURRENT_ACCOUNT_ADDRESS_KEY, address)
        .apply()
  }

  override fun isFirstTimeOnTransactionActivity(): Boolean {
    return pref.getBoolean(FIRST_TIME_ON_TRANSACTION_ACTIVITY_KEY, false)
  }

  override fun setFirstTimeOnTransactionActivity() {
    pref.edit()
        .putBoolean(FIRST_TIME_ON_TRANSACTION_ACTIVITY_KEY, true)
        .apply()
  }

  override fun saveAutoUpdateCardDismiss(updateVersionCode: Int): Completable {
    return Completable.fromCallable {
      pref.edit()
          .putInt(AUTO_UPDATE_VERSION, updateVersionCode)
          .apply()
    }
  }

  override fun getAutoUpdateCardDismissedVersion(): Single<Int> {
    return Single.fromCallable { pref.getInt(AUTO_UPDATE_VERSION, 0) }
  }

  override fun clearPoaNotificationSeenTime() {
    pref.edit()
        .remove(POA_LIMIT_SEEN_TIME)
        .apply()
  }

  override fun getPoaNotificationSeenTime() = pref.getLong(POA_LIMIT_SEEN_TIME, -1)

  override fun setPoaNotificationSeenTime(currentTimeInMillis: Long) {
    pref.edit()
        .putLong(POA_LIMIT_SEEN_TIME, currentTimeInMillis)
        .apply()
  }

  override fun setUpdateNotificationSeenTime(currentTimeMillis: Long) {
    pref.edit()
        .putLong(UPDATE_SEEN_TIME, currentTimeMillis)
        .apply()
  }

  override fun getUpdateNotificationSeenTime() = pref.getLong(UPDATE_SEEN_TIME, -1)

  override fun setWalletValidationStatus(walletAddress: String, validated: Boolean) {
    pref.edit()
        .putBoolean(WALLET_VERIFIED + walletAddress, validated)
        .apply()
  }

  override fun isWalletValidated(walletAddress: String) =
      pref.getBoolean(WALLET_VERIFIED + walletAddress, false)

  override fun removeWalletValidationStatus(walletAddress: String) {
    pref.edit()
        .remove(WALLET_VERIFIED + walletAddress)
        .apply()
  }

  override fun addWalletPreference(address: String?) {
    pref.edit()
        .putString(PREF_WALLET, address)
        .apply()
  }

  override fun getBackupNotificationSeenTime(walletAddress: String) =
      pref.getLong(BACKUP_SEEN_TIME + walletAddress, -1)

  override fun setBackupNotificationSeenTime(walletAddress: String, currentTimeMillis: Long) {
    pref.edit()
        .putLong(BACKUP_SEEN_TIME + walletAddress, currentTimeMillis)
        .apply()
  }

  override fun removeBackupNotificationSeenTime(walletAddress: String) {
    pref.edit()
        .remove(BACKUP_SEEN_TIME + walletAddress)
        .apply()
  }

  override fun isWalletImportBackup(walletAddress: String) =
      pref.getBoolean(WALLET_IMPORT_BACKUP + walletAddress, false)

  override fun setWalletImportBackup(walletAddress: String) {
    pref.edit()
        .putBoolean(WALLET_IMPORT_BACKUP + walletAddress, true)
        .apply()
  }

  override fun removeWalletImportBackup(walletAddress: String) {
    pref.edit()
        .remove(WALLET_IMPORT_BACKUP + walletAddress)
        .apply()
  }

  override fun hasShownBackup(walletAddress: String): Boolean {
    return pref.getBoolean(HAS_SHOWN_BACKUP + walletAddress, false)
  }

  override fun setHasShownBackup(walletAddress: String, hasShown: Boolean) {
    pref.edit()
        .putBoolean(HAS_SHOWN_BACKUP + walletAddress, hasShown)
        .apply()
  }

  companion object {

    private const val CURRENT_ACCOUNT_ADDRESS_KEY = "current_account_address"
    private const val ONBOARDING_COMPLETE_KEY = "onboarding_complete"
    private const val ONBOARDING_SKIP_CLICKED_KEY = "onboarding_skip_clicked"
    private const val FIRST_TIME_ON_TRANSACTION_ACTIVITY_KEY = "first_time_on_transaction_activity"
    private const val AUTO_UPDATE_VERSION = "auto_update_version"
    private const val POA_LIMIT_SEEN_TIME = "poa_limit_seen_time"
    private const val UPDATE_SEEN_TIME = "update_seen_time"
    private const val BACKUP_SEEN_TIME = "backup_seen_time_"
    private const val WALLET_VERIFIED = "wallet_verified_"
    private const val PREF_WALLET = "pref_wallet"
    private const val WALLET_IMPORT_BACKUP = "wallet_import_backup_"
    private const val HAS_SHOWN_BACKUP = "has_shown_backup_"
  }
}
