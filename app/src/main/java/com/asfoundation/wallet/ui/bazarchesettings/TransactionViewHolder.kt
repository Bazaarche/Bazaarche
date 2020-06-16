package com.asfoundation.wallet.ui.bazarchesettings

import android.graphics.drawable.Drawable
import android.view.View
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import com.asfoundation.wallet.transactions.Transaction
import com.asfoundation.wallet.ui.toggleVisibility
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_bazaarche_transaction.view.*

class TransactionViewHolder(itemView: View, private val defaultWalletAddress: String,
                            clickListener: (Int) -> Unit) :
    BaseAdapter.BaseViewHolder<Transaction>(itemView, clickListener) {

  private var requestListener: RequestListener<Drawable?>? = null

  override fun bind(item: Transaction) {
    with(item) {

      val uri: String? = getUri()

      itemView.imageTransactionType.toggleVisibility(visible = isTransactionTypeShouldBeShown(uri))

      val transactionTypeIcon = when (type) {
        Transaction.TransactionType.IAB, Transaction.TransactionType.IAP_OFFCHAIN -> {
          R.drawable.ic_transaction_iab
        }
        Transaction.TransactionType.ADS, Transaction.TransactionType.ADS_OFFCHAIN -> {
          R.drawable.ic_transaction_poa
        }
        Transaction.TransactionType.BONUS -> {
          R.drawable.ic_transaction_peer
        }
        else -> {
          R.drawable.ic_transaction_peer
        }
      }

      itemView.textDescription.text = details?.description ?: ""

      itemView.textAddress.text = getAddressText(itemView.context, defaultWalletAddress)

      requestListener = object : RequestListener<Drawable?> {
        override fun onLoadFailed(exception: GlideException?, model: Any,
                                  target: Target<Drawable?>,
                                  isFirstResource: Boolean): Boolean {

          itemView.imageTransactionType.visibility = View.GONE
          return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any,
                                     target: Target<Drawable?>,
                                     dataSource: DataSource,
                                     isFirstResource: Boolean): Boolean {
          itemView.imageTransactionType.setImageResource(transactionTypeIcon)
          return false
        }
      }

      GlideApp.with(itemView.context)
          .load(uri)
          .apply(RequestOptions.bitmapTransform(CircleCrop())
              .placeholder(transactionTypeIcon)
              .error(transactionTypeIcon))
          .listener(requestListener)
          .into(itemView.imageSource)

    }
  }

  fun onViewRecycled() {
    requestListener = null
  }

}