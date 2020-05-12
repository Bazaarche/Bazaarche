package com.asfoundation.wallet.ui.bazarchesettings

import android.view.View
import com.asf.wallet.R
import com.asfoundation.wallet.transactions.Transaction

class TransactionsAdapter(private val defaultWalletAddress: String,
                          items: List<Transaction>,
                          private val clickListener: (Int) -> Unit) :
    BaseAdapter<Transaction, TransactionViewHolder>(items) {

  override fun onViewRecycled(holder: TransactionViewHolder) {

    holder.onViewRecycled()
  }

  override fun getLayoutId(position: Int): Int = R.layout.item_bazaarche_transaction

  override fun createViewHolder(view: View, viewType: Int): TransactionViewHolder =
      TransactionViewHolder(view, defaultWalletAddress, clickListener)

}