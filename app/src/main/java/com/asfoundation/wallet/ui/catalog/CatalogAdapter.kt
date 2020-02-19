package com.asfoundation.wallet.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.navigator.AppsNavigator

class CatalogAdapter(private val appsNavigator: AppsNavigator) : RecyclerView.Adapter<CatalogViewHolder<CatalogItem>>() {

  val items = mutableListOf<CatalogItem>()

  override fun getItemViewType(position: Int): Int =
      when (items[position]) {
        is Hami -> {
          R.layout.item_hami
        }
        is PromoItem -> {
          R.layout.item_promo
        }
        is Header -> {
          R.layout.item_apps_header
        }
        is AppItem -> {
          R.layout.item_app
        }
      }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder<CatalogItem> {

    val itemView = LayoutInflater.from(parent.context)
        .inflate(viewType, parent, false)

    return CatalogViewHolder.getViewHolder(viewType, itemView, appsNavigator)
  }

  override fun onBindViewHolder(viewHolder: CatalogViewHolder<CatalogItem>, position: Int) {

    viewHolder.bind(items[position])
  }

  override fun getItemCount(): Int = items.size

  fun addItems(items: List<CatalogItem>) {
    this.items.addAll(items)
    notifyDataSetChanged()
  }

}
