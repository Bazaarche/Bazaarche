package com.asfoundation.wallet.ui.bazarchesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.wallet.R
import com.asfoundation.wallet.entity.Result
import com.asfoundation.wallet.transactions.Transaction
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_transactions.*
import javax.inject.Inject

class TransactionsFragment : DaggerFragment() {

  @Inject
  lateinit var transactionsViewModelFactory: TransactionsViewModelFactory
  private lateinit var transactionsViewModel: TransactionsViewModel

  private lateinit var adapter: TransactionsAdapter

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
        .observe(viewLifecycleOwner, Observer(::onTransactionAndWalletReady))
  }

  private fun setupRecyclerView() {
    recyclerTransactions.apply {
      loadingView = progressTransactions
      emptyView = textNoTransactions

      layoutManager = LinearLayoutManager(requireContext())
    }
  }

  private fun initViewModel() {
    transactionsViewModel = ViewModelProviders.of(this,
        transactionsViewModelFactory)[TransactionsViewModel::class.java]
  }

  private fun onTransactionAndWalletReady(
      transactionAndWallet: Result<Pair<String, List<Transaction>>>) {

    recyclerTransactions.isLoading = false

    when (transactionAndWallet) {
      Result.Loading -> {
        recyclerTransactions.isLoading = true
      }
      is Result.Error -> {
        //TODO
      }
      is Result.Success -> {
        adapter = TransactionsAdapter(transactionAndWallet.data.first,
            transactionAndWallet.data.second)
        recyclerTransactions.adapter = adapter
      }
    }
  }

}