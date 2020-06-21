package com.asfoundation.wallet.ui.bazarchesettings.backuprestore

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.asf.wallet.R
import com.asfoundation.wallet.C.IMPORT_REQUEST_CODE
import com.asfoundation.wallet.entity.Wallet
import com.asfoundation.wallet.ui.ImportWalletActivity
import com.asfoundation.wallet.util.KeyboardUtils
import com.asfoundation.wallet.util.observe
import com.asfoundation.wallet.viewmodel.WalletsViewModel
import com.asfoundation.wallet.viewmodel.WalletsViewModelFactory
import com.asfoundation.wallet.widget.BackupView
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_wallet.*
import javax.inject.Inject

class WalletFragment : DaggerFragment() {

  @Inject
  lateinit var viewModelFactory: WalletsViewModelFactory

  private val viewModel: WalletsViewModel by lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProviders.of(this, viewModelFactory)
        .get(WalletsViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater.inflate(R.layout.fragment_wallet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    requireActivity().setTitle(R.string.wallet)
    observeViewModels()
    setClickListeners()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == IMPORT_REQUEST_CODE && resultCode == RESULT_OK) {

      onImportSucceeded()
    }
  }

  private fun observeViewModels() {
    viewModel.defaultWallet()
        .observe(viewLifecycleOwner, ::onWalletReady)

    viewModel.exportedStore()
        .observe(viewLifecycleOwner, ::shareWallet)
  }

  private fun setClickListeners() {

    fun startImportWalletActivity() {
      val intent = Intent(requireContext(), ImportWalletActivity::class.java)
      startActivityForResult(intent, IMPORT_REQUEST_CODE)
    }

    textRestore.setOnClickListener {
      startImportWalletActivity()
    }

    textBackup.setOnClickListener {
      showBackupDialog()
    }
  }

  private fun onImportSucceeded() {
    Snackbar.make(requireView(),
        R.string.toast_message_wallet_imported,
        Snackbar.LENGTH_SHORT)
        .show()

    viewModel.showTransactions(requireContext())
  }

  private fun onWalletReady(wallet: Wallet) {
    textWalletId.text = wallet.address
    textBackup.isEnabled = true//Enable backup when wallet ready
  }

  private fun shareWallet(walletData: String) {
    val shareIntent = viewModel.getShareIntent(walletData)
    val chooserTitle = getString(R.string.share_via)
    startActivityForResult(Intent.createChooser(shareIntent, chooserTitle), 0)
  }

  private fun showBackupDialog() {

    val view = BackupView(requireContext())
    AlertDialog.Builder(requireContext())
        .setView(view)
        .setPositiveButton(R.string.ok) { _, _ ->
          viewModel.exportWallet(view.password)
          KeyboardUtils.hideKeyboard(view)
        }
        .setNegativeButton(R.string.cancel) { _, _ ->
          KeyboardUtils.hideKeyboard(view)
        }
        .setOnDismissListener {
          KeyboardUtils.hideKeyboard(view)
        }
        .show()

  }

}
