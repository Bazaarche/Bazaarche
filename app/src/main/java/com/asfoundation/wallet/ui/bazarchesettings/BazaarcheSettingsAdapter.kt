package com.asfoundation.wallet.ui.bazarchesettings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import kotlinx.android.synthetic.main.item_bazaarche_settings.view.*

class BazaarcheSettingsAdapter(private val items: Array<Int>, private val clickListener: (Int) -> Unit) : RecyclerView.Adapter<BazaarcheSettingsAdapter.ViewHolder>() {


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val itemView = inflater.inflate(R.layout.item_bazaarche_settings, parent, false)

    return ViewHolder(itemView, clickListener)
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val textRes = items[position]
    holder.bind(textRes)
  }


  class ViewHolder(itemView: View, clickListener: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {

    init {
      itemView.setOnClickListener { clickListener(adapterPosition) }
    }

    internal fun bind(@StringRes textRes: Int) {

      itemView.textItemBazaarcheSettings.setText(textRes)
    }
  }

}
