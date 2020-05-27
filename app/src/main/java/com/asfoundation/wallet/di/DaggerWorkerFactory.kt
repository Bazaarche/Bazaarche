package com.asfoundation.wallet.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DaggerWorkerFactory
@Inject
constructor(private val workerSubComponent: WorkerSubComponent.Builder) : WorkerFactory() {
  override fun createWorker(appContext: Context, workerClassName: String,
                            workerParameters: WorkerParameters): ListenableWorker {
    return workerSubComponent.workerParameters(workerParameters)
        .build()
        .run {
          createWorker(workerClassName, workers())
        }
  }

  private fun createWorker(workerClassName: String,
                           workers: Map<Class<out RxWorker>, Provider<RxWorker>>): ListenableWorker {
    val workerClass = Class.forName(workerClassName)
        .asSubclass(RxWorker::class.java)
    var provider = workers[workerClass]
    if (provider == null) {
      for ((key, value) in workers) {
        if (workerClass.isAssignableFrom(key)) {
          provider = value
          break
        }
      }
    }
    requireNotNull(provider) { "Missing binding for $workerClassName" }
    return provider.get()
  }
}