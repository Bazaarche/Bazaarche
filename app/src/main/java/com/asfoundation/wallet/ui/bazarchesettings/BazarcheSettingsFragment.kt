package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import com.asfoundation.wallet.ui.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_bazaarche_settings.view.*


class BazarcheSettingsFragment : BaseFragment() {

  val items = arrayOf(R.string.transactions_list, R.string.language_settings, R.string.bazaarche_guide, R.string.support)

  private val itemClickListener: (Int) -> Unit = { position ->

    when (items[position]) {
      R.string.transactions_list -> {
        interactionListener.onTransactionsClicked()
      }
      R.string.language_settings -> {
        interactionListener.onLanguageSettingsClicked()
      }
      R.string.bazaarche_guide -> {
        interactionListener.onGuideClicked()
      }
      R.string.support -> {
        interactionListener.onSupportClicked()
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_bazaarche_settings, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    setupRecyclerView()
  }

  private fun setupRecyclerView() {

    requireView().recycler_bazaarche_settings.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = BazaarcheSettingsAdapter(items, itemClickListener)
      val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
      addItemDecoration(divider)
    }
  }

}
