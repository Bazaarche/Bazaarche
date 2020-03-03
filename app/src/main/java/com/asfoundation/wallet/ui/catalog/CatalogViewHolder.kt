package com.asfoundation.wallet.ui.catalog

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.navigator.AppsNavigator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_app.view.*
import kotlinx.android.synthetic.main.item_apps_header.view.*
import kotlinx.android.synthetic.main.item_hami.view.*
import kotlinx.android.synthetic.main.item_promo.view.*

abstract class CatalogViewHolder<T : CatalogItem>(itemView: View, protected val clickListener: (T) -> Unit) : RecyclerView.ViewHolder(itemView) {

  companion object {
    internal fun getViewHolder(viewType: Int, itemView: View, appsNavigator: AppsNavigator): CatalogViewHolder<CatalogItem> {
      @Suppress("UNCHECKED_CAST")
      return when (viewType) {
        R.layout.item_hami -> {
          HamiViewHolder(itemView, appsNavigator::onHamiClicked)
        }
        R.layout.item_promo -> {
          PromoViewHolder(itemView, appsNavigator::onPromoClicked)
        }
        R.layout.item_apps_header -> {
          HeaderViewHolder(itemView, appsNavigator::onHeaderClicked)
        }
        R.layout.item_app -> {
          AppViewHolder(itemView, appsNavigator::onAppClicked)
        }
        else -> throw IllegalStateException("Invalid viewType $viewType")
      } as CatalogViewHolder<CatalogItem>
    }
  }

  abstract fun bind(catalogItem: T)
  protected fun showImage(imageURL: String, imageView: ImageView) {
    Picasso.with(itemView.context)
        .load(imageURL)
        .into(imageView)
  }

  private class HamiViewHolder(itemView: View, clickListener: (Hami) -> Unit) : CatalogViewHolder<Hami>(itemView, clickListener) {
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

  private class PromoViewHolder(itemView: View, clickListener: (PromoItem) -> Unit) : CatalogViewHolder<PromoItem>(itemView, clickListener) {

    override fun bind(catalogItem: PromoItem) {

      catalogItem.apply {

        showImage(imageURL, itemView.imagePromo)
        itemView.setOnClickListener { clickListener(catalogItem) }
      }
    }

  }

  private class HeaderViewHolder(itemView: View, clickListener: (Header) -> Unit) : CatalogViewHolder<Header>(itemView, clickListener) {
    override fun bind(catalogItem: Header) {

      itemView.textHeaderName.text = catalogItem.title
      itemView.imageHeaderMore.setOnClickListener { clickListener(catalogItem) }
    }

  }

  private class AppViewHolder(itemView: View, clickListener: (AppItem) -> Unit) : CatalogViewHolder<AppItem>(itemView, clickListener) {

    override fun bind(catalogItem: AppItem) {

      itemView.textApp.text = catalogItem.name
      showImage(catalogItem.image, itemView.imageApp)
      itemView.setOnClickListener { clickListener(catalogItem) }
    }

  }

}