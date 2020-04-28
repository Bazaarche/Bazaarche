package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_transactions.*
import javax.inject.Inject

class TransactionsFragment : DaggerFragment() {

  @Inject
  lateinit var transactionsViewModelFactory: TransactionsViewModelFactory
  private lateinit var transactionsViewModel: TransactionsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    initViewModel()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater.inflate(R.layout.fragment_transactions, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setupRecyclerView()

    transactionsViewModel.getTransactionsAndWalletLiveData()
        .observe(viewLifecycleOwner, Observer {/*TODO*/})
  }

  private fun setupRecyclerView() {
    recyclerTransactions.apply {

      layoutManager = LinearLayoutManager(requireContext())
    }
  }

  private fun initViewModel() {
    transactionsViewModel = ViewModelProviders.of(this,
        transactionsViewModelFactory)[TransactionsViewModel::class.java]
  }

}