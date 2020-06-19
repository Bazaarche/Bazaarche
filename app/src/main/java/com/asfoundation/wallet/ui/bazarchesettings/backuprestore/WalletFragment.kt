package com.asfoundation.wallet.ui.bazarchesettings.backuprestore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.asf.wallet.R
import com.asfoundation.wallet.entity.Wallet
import com.asfoundation.wallet.util.observe
import com.asfoundation.wallet.viewmodel.WalletsViewModel
import com.asfoundation.wallet.viewmodel.WalletsViewModelFactory
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
  }

  private fun observeViewModels() {
    viewModel.defaultWallet()
        .observe(viewLifecycleOwner, ::onWalletReady)
  }

  private fun onWalletReady(wallet: Wallet) {
    textWalletId.text = wallet.address
  }

}
