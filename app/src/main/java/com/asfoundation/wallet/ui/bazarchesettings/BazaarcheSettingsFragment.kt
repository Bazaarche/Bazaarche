package com.asfoundation.wallet.ui.bazarchesettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import com.asfoundation.wallet.router.ManageWalletsRouter
import com.asfoundation.wallet.ui.SplashActivity
import com.asfoundation.wallet.ui.createItemDecoration
import com.asfoundation.wallet.util.languagecontroller.Language
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_bazaarche_settings.view.*
import com.asfoundation.wallet.ui.bazarchesettings.SettingsItem.*
import javax.inject.Inject


class BazaarcheSettingsFragment : DaggerFragment() {

  @Inject
  internal lateinit var manageWalletsRouter: ManageWalletsRouter

  private val itemClickListener: (Int) -> Unit = { position ->

    when (items[position]) {
      ACTION_WALLETS -> {
        manageWalletsRouter.open(requireContext())
      }
      TRANSACTIONS_LIST -> {
        openTransactionsFragment()
      }
      LANGUAGE_SETTINGS -> {
        showChangeLanguageDialog()
      }
      BAZAARCHE_GUIDE -> {
        //TODO
      }
      SUPPORT -> {
        //TODO
      }
    }
  }

  private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProviders.of(this)[BazaarcheSettingsViewModel::class.java]
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_bazaarche_settings, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    setupRecyclerView()
  }

  private fun openTransactionsFragment() {
    parentFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, TransactionsFragment())
        .addToBackStack(null)
        .commit()
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

    val languages = Language.values()
        .map {
          getString(it.titleRes)
        }
        .toTypedArray()

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
