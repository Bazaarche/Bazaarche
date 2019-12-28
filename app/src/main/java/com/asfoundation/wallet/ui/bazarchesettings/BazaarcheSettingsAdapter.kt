package com.asfoundation.wallet.ui.bazarchesettings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import kotlinx.android.synthetic.main.item_bazaarche_settings.view.*

class BazaarcheSettingsAdapter(private val textResources: Array<Int>) : RecyclerView.Adapter<BazaarcheSettingsAdapter.ViewHolder>() {


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val itemView = inflater.inflate(R.layout.item_bazaarche_settings, parent, false)

    return ViewHolder(itemView)
  }

  override fun getItemCount(): Int = textResources.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val textRes = textResources[position]
    holder.bind(textRes)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal fun bind(@StringRes textRes: Int) {

      itemView.text_item_bazaarche_settings.setText(textRes)
    }
  }
}
