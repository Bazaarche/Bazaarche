package com.asfoundation.wallet.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.asfoundation.wallet.ui.toggleVisibility
import kotlin.properties.Delegates.observable

class EmptyAndLoadingRecyclerView : RecyclerView {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
      super(context, attrs, defStyle)

  var emptyView: View? = null
    set(value) {
      field = value
      changeVisibilitiesBasedOnState()
    }

  var loadingView: View? = null
    set(value) {
      field = value
      changeVisibilitiesBasedOnState()
    }

  var errorView: View? = null
    set(value) {
      field = value
      changeVisibilitiesBasedOnState()
    }

  private var state: ViewState by observable(ViewState.Loading, { _, oldValue, newValue ->
    if (newValue != oldValue) {
      changeVisibilitiesBasedOnState()
    }
  })

  private val observer = object : AdapterDataObserver() {
    override fun onChanged() {
      checkEmpty()
    }
  }

  /**
   * Shows [loadingView]. Ignores call if recyclerView has data.
   * Automatically hide [loadingView] when recyclerView got data
   */
  fun showLoading() {
    if (state != ViewState.HasData) {
      state = ViewState.Loading
    }
  }

  /**
   * Shows [errorView]. Ignores call if recyclerView has data.
   * Automatically hide [errorView] when recyclerView got data
   */
  fun showError() {
    if (state != ViewState.HasData) {
      state = ViewState.Error
    }
  }

  override fun setAdapter(adapter: Adapter<*>?) {
    getAdapter()?.unregisterAdapterDataObserver(observer)
    super.setAdapter(adapter)
    adapter?.registerAdapterDataObserver(observer)
    checkEmpty()
  }

  private fun changeVisibilitiesBasedOnState() {

    emptyView?.toggleVisibility(state == ViewState.Empty)
    loadingView?.toggleVisibility(state == ViewState.Loading)
    errorView?.toggleVisibility(state == ViewState.Error)
  }

  private fun checkEmpty() {
    val isAdapterEmpty = adapter?.itemCount ?: 0 <= 0
    state = if (isAdapterEmpty) {
      ViewState.Empty
    } else {
      ViewState.HasData
    }
  }

}

private enum class ViewState {
  Error,
  Loading,
  Empty,
  HasData
}