package com.asfoundation.wallet.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EmptyAndLoadingRecyclerView : RecyclerView {

  var emptyView: View? = null
    set(value) {
      field = value
      checkEmptyAndLoading()
    }

  private val isEmpty: Boolean
    get() {
      return adapter?.itemCount ?: 0 <= 0
    }

  var loadingView: View? = null
    set(value) {
      field = value
      checkEmptyAndLoading()
    }

  var isLoading: Boolean = false
    set(value) {
      field = value
      checkEmptyAndLoading()
    }

  private val observer: AdapterDataObserver = object : AdapterDataObserver() {
    override fun onChanged() {
      checkEmptyAndLoading()
    }
  }

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
      super(context, attrs, defStyle)

  override fun setAdapter(adapter: Adapter<*>?) {
    getAdapter()?.unregisterAdapterDataObserver(observer)
    super.setAdapter(adapter)
    adapter?.registerAdapterDataObserver(observer)
    checkEmptyAndLoading()
  }

  private fun checkEmptyAndLoading() {
    if (isEmpty) {
      if (isLoading) {
        loadingView?.visibility = View.VISIBLE
        emptyView?.visibility = View.GONE
      } else {
        emptyView?.visibility = View.VISIBLE
        loadingView?.visibility = View.GONE
      }
    } else {
      emptyView?.visibility = View.GONE
      loadingView?.visibility = View.GONE
    }
  }

}