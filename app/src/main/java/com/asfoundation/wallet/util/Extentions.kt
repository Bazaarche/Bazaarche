package com.asfoundation.wallet.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

inline fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, crossinline function : (T) -> Unit) {

  observe(lifecycleOwner, Observer { function(it) })
}

/**
 * @see ClipData.newPlainText()
 */
fun CharSequence.copyToClipboard(context: Context, label: CharSequence = "text") {
  val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
  val clip = ClipData.newPlainText(label, this)
  clipboard?.primaryClip = clip
}