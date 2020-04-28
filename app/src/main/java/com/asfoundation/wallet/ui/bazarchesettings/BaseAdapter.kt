package com.asfoundation.wallet.ui.bazarchesettings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * BaseAdapter For simple use cases.
 */
abstract class BaseAdapter<E : Any, VH : BaseAdapter.BaseViewHolder<E>>(
    items: List<E> = emptyList(),
    private val clickListener: ((E) -> Unit)? = null) : RecyclerView.Adapter<VH>() {

  val items: MutableList<E> = items.toMutableList()

  fun addItems(items: List<E>) {
    val oldSize = this.items.size
    this.items.addAll(items)
    notifyItemRangeInserted(oldSize, items.size)
  }

  final override fun getItemCount(): Int = items.size

  final override fun getItemViewType(position: Int): Int {
    return getLayoutId(position)
  }

  @LayoutRes
  abstract fun getLayoutId(position: Int): Int
  abstract fun createViewHolder(view: View, viewType: Int): VH

  final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val itemView = LayoutInflater.from(parent.context)
        .inflate(viewType, parent, false)
    return createViewHolder(itemView, viewType)
  }

  final override fun onBindViewHolder(holder: VH, position: Int) {
    val item = items[position]
    holder.item = item
    holder.clickListener = clickListener
    holder.bind(item)
  }

  abstract class BaseViewHolder<E : Any>(itemView: View) :
      RecyclerView.ViewHolder(itemView) {

    internal lateinit var item : E
    internal var clickListener: ((E) -> Unit)? = null
    set(value) {
      field = value
      itemView.setOnClickListener { field?.invoke(item) }
    }

    abstract fun bind(item : E)
  }
}