package com.asfoundation.wallet.ui.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_app.view.*
import kotlinx.android.synthetic.main.item_apps_header.view.*
import kotlinx.android.synthetic.main.item_hami.view.*

class CatalogAdapter(private val onCatalogItemClicked: OnCatalogItemClicked) : RecyclerView.Adapter<CatalogAdapter.ViewHolder<CatalogItem>>() {

  val items = mutableListOf<CatalogItem>()

  override fun getItemViewType(position: Int): Int =
      when (items[position]) {
        is Hami -> {
          R.layout.item_hami
        }
        is Header -> {
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
        HamiViewHolder(itemView, onCatalogItemClicked::onHamiClicked)
      }
      R.layout.item_apps_header -> {
        HeaderViewHolder(itemView, onCatalogItemClicked::onHeaderClicked)
      }
      else -> {
        AppViewHolder(itemView, onCatalogItemClicked::onAppClicked)
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

  abstract class ViewHolder<T : CatalogItem>(itemView: View, protected val clickListener: (T) -> Unit) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(catalogItem: T)

    internal fun showImage(what: String, into: ImageView) {
      Picasso.with(itemView.context)
          .load(what)
          .into(into)
    }
  }

  private class HamiViewHolder(itemView: View, clickListener: (Hami) -> Unit) : ViewHolder<Hami>(itemView, clickListener) {
    override fun bind(catalogItem: Hami) {

      catalogItem.apply {

        showHamiImage(imageURL)
        showImage(app.image, itemView.imageHamiAppIcon)
        itemView.textHamiAppName.text = app.name
        showShortDescription(this)
        itemView.setOnClickListener { clickListener(catalogItem) }
      }
    }

    private fun showShortDescription(hami: Hami) {
      if (hami.shortDescription.isEmpty()) {
        itemView.textHamiShortDescription.visibility = View.GONE
      } else {
        itemView.textHamiShortDescription.text = hami.shortDescription
      }
    }

    private fun showHamiImage(imageURL: String) {
      Picasso.with(itemView.context)
          .load(imageURL)
          .placeholder(R.drawable.bg_sample_app)
          .into(itemView.imageHami)
    }
  }

  private class HeaderViewHolder(itemView: View, clickListener: (Header) -> Unit) : ViewHolder<Header>(itemView, clickListener) {
    override fun bind(catalogItem: Header) {

      itemView.textHeaderName.text = catalogItem.title
      itemView.imageHeaderMore.setOnClickListener { clickListener(catalogItem) }
    }

  }

  private class AppViewHolder(itemView: View, clickListener: (AppItem) -> Unit) : ViewHolder<AppItem>(itemView, clickListener) {

    override fun bind(catalogItem: AppItem) {

      itemView.textApp.text = catalogItem.name
      showImage(catalogItem.image, itemView.imageApp)
      itemView.setOnClickListener { clickListener(catalogItem) }
    }

  }

  interface OnCatalogItemClicked {

    fun onHamiClicked(hami: Hami)

    fun onHeaderClicked(header: Header)

    fun onAppClicked(appItem: AppItem)
  }
}
