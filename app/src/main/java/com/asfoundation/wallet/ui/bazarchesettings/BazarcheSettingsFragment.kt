package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import com.asfoundation.wallet.ui.createItemDecoration
import kotlinx.android.synthetic.main.fragment_bazaarche_settings.view.*


class BazarcheSettingsFragment : Fragment() {

  private val items = arrayOf(R.string.transactions_list, R.string.language_settings, R.string.bazaarche_guide, R.string.support)

  private val itemClickListener: (Int) -> Unit = { position ->

    when (items[position]) {
      R.string.transactions_list -> {
        //TODO
      }
      R.string.language_settings -> {
        //TODO
      }
      R.string.bazaarche_guide -> {
        //TODO
      }
      R.string.support -> {
        //TODO
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

    requireView().recyclerBazaarcheSettings.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = BazaarcheSettingsAdapter(items, itemClickListener)

      val itemDecoration = createItemDecoration()
      addItemDecoration(itemDecoration)
    }
  }

  private fun createItemDecoration(): DividerItemDecoration {

    val margin = resources.getDimensionPixelSize(R.dimen.default_margin_double)
    return createItemDecoration(requireContext(), margin, margin)
  }

}
