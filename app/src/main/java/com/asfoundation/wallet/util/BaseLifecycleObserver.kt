package com.asfoundation.wallet.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

/**
 * LifeCycleAwareComponent that adds itself as [LifecycleObserver] to [Lifecycle] in constructor
 * @author Seyyed Davud Hosseini
 */
open class BaseLifecycleObserver(lifecycle: Lifecycle) : LifecycleObserver {

  init {
    addObserver(lifecycle)
  }

  private fun addObserver(lifecycle: Lifecycle) {
    lifecycle.addObserver(this)
  }
}
