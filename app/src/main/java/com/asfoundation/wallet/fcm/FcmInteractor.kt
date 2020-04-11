package com.asfoundation.wallet.fcm

import com.asfoundation.wallet.repository.AutoUpdateRepository
import javax.inject.Inject

class FcmInteractor @Inject constructor(private var autoUpdateRepository: AutoUpdateRepository) {

  internal fun handleDataNotification(data: Map<String, String>) {
    data[APP_LOWEST_SUPPORTED_VERSION]?.also {
      handleForceUpdate(it.toInt())
    }
  }

  private fun handleForceUpdate(lowestSupportedVersion: Int) {
    autoUpdateRepository.saveLowestSupportedVersion(lowestSupportedVersion)
  }

  companion object {
    private const val APP_LOWEST_SUPPORTED_VERSION = "lowest_supported_version"
  }
}