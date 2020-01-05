package com.asfoundation.wallet.ui.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_app.view.*
import kotlinx.android.synthetic.main.item_apps_header.view.*
import kotlinx.android.synthetic.main.item_hami.view.*

class CatalogAdapter : RecyclerView.Adapter<CatalogAdapter.ViewHolder<CatalogItem>>() {

  val items = mutableListOf<CatalogItem>()

  override fun getItemViewType(position: Int): Int =
      when {
        items[position] is Hami -> {
          R.layout.item_hami
        }
        items[position] is Header -> {
          R.layout.item_apps_header
        }
        else -> {
          R.layout.item_app
        }
      }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<CatalogItem> {

    val itemView = LayoutInflater.from(parent.context)
        .inflate(viewType, parent, false)

    @Suppress("UNCHECKED_CAST")
    return when (viewType) {
      R.layout.item_hami -> {
        HamiViewHolder(itemView)
      }
      R.layout.item_apps_header -> {
        HeaderViewHolder(itemView)
      }
      else -> {
        AppViewHolder(itemView)
      }
    } as ViewHolder<CatalogItem>

  }

  override fun onBindViewHolder(viewHolder: ViewHolder<CatalogItem>, position: Int) {

    viewHolder.bind(items[position])
  }

  override fun getItemCount(): Int = items.size

  fun addItems(items: List<CatalogItem>) {
    this.items.addAll(items)
    notifyDataSetChanged()
  }

  abstract class ViewHolder<T : CatalogItem>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(catalogItem: T)
  }

  class HamiViewHolder(itemView: View) : ViewHolder<Hami>(itemView) {
    override fun bind(catalogItem: Hami) {

      catalogItem.apply {

        showHamiImage(imageURL)
        itemView.textHamiTitle.text = title
        itemView.textHamiDescription.text = shortDescription
        itemView.textHamiMore.setOnClickListener { /*TODO*/ }
      }
    }


    private fun showHamiImage(imageURL: String) {
      Picasso.with(itemView.context)
          .load(imageURL)
          .into(itemView.imageHami)
    }
  }

  class HeaderViewHolder(itemView: View) : ViewHolder<Header>(itemView) {
    override fun bind(catalogItem: Header) {

      itemView.textHeaderName.text = catalogItem.title
      itemView.imageHeaderMore.setOnClickListener { /*TODO*/ }
    }

  }

  class AppViewHolder(itemView: View) : ViewHolder<AppItem>(itemView) {

    override fun bind(catalogItem: AppItem) {

      itemView.textApp.text = catalogItem.name
      showAppImage(catalogItem)
      itemView.setOnClickListener { /*TODO*/ }
    }

    private fun showAppImage(catalogItem: AppItem) {
      Picasso.with(itemView.context)
          .load(catalogItem.image)
          .into(itemView.imageApp)
    }
  }

}
