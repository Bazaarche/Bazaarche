package com.asfoundation.wallet.ui.bazarchesettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import com.asfoundation.wallet.ui.SplashActivity
import com.asfoundation.wallet.ui.createItemDecoration
import com.asfoundation.wallet.util.languagecontroller.Language
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_bazaarche_settings.view.*


class BazarcheSettingsFragment : Fragment() {

  private val items = arrayOf(R.string.action_wallets, R.string.transactions_list,
      R.string.language_settings, R.string.bazaarche_guide, R.string.support)

  private val itemClickListener: (Int) -> Unit = { position ->

    when (items[position]) {
      R.string.action_wallets -> {
        //TODO
      }
      R.string.transactions_list -> {
        //TODO
      }
      R.string.language_settings -> {
        showChangeLanguageDialog()
      }
      R.string.bazaarche_guide -> {
        //TODO
      }
      R.string.support -> {
        //TODO
      }
    }
  }

  private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProviders.of(this)[BazaarcheSettingsViewModel::class.java]
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

  private fun showChangeLanguageDialog() {

    val languages = Language.values().map {
      getString(it.titleRes)
    }.toTypedArray()

    var selectedPosition = viewModel.getSelectedLanguagePosition()

    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.choose_language)
        .setSingleChoiceItems(languages, selectedPosition) { _, position ->
          selectedPosition = position
        }
        .setNegativeButton(R.string.close, null)
        .setPositiveButton(R.string.confirm) { _, _ -> onLanguageSelected(selectedPosition) }
        .show()
  }

  private fun onLanguageSelected(selectedPosition: Int) {

    fun restartApplication() {
      requireActivity().finish()

      val intent = Intent(requireContext(), SplashActivity::class.java)
      requireActivity().startActivity(intent)
    }

    viewModel.restartObservable.observe(this, Observer { restartNeeded ->
      if (restartNeeded) {
        restartApplication()
      }
    })

    viewModel.onLanguageSelected(requireContext(), selectedPosition)
  }

}
