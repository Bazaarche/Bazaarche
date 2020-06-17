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
import com.asfoundation.wallet.ui.createItemDecoration
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_transactions.*
import javax.inject.Inject

class TransactionsFragment : DaggerFragment() {

  @Inject
  lateinit var transactionsViewModelFactory: TransactionsViewModelFactory
  private lateinit var transactionsViewModel: TransactionsViewModel

  private var adapter: TransactionsAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    initViewModel()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    return inflater.inflate(R.layout.fragment_transactions, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setTitle()
    setupRecyclerView()
    setTryAgainClickListener()

    transactionsViewModel.transactionsAndWallet
        .observe(viewLifecycleOwner, Observer(::onTransactionAndWalletReady))
  }

  override fun onDestroyView() {
    adapter = null
    super.onDestroyView()
  }

  private fun initViewModel() {
    transactionsViewModel = ViewModelProviders.of(this,
        transactionsViewModelFactory)[TransactionsViewModel::class.java]
  }

  private fun setTitle() {
      requireActivity().setTitle(R.string.transactions_list)
  }

  private fun setupRecyclerView() {
    recyclerTransactions.apply {
      loadingView = progressTransactions
      emptyView = textNoTransactions
      errorView = groupTransactionError
      layoutManager = LinearLayoutManager(requireContext())

      val itemDecorationStartMargin = resources.getDimensionPixelSize(R.dimen.source_image_size)
      addItemDecoration(createItemDecoration(requireContext(), 0, itemDecorationStartMargin))
    }
  }

  private fun setTryAgainClickListener() {
    buttonTransactionsTryAgain.setOnClickListener {
      transactionsViewModel.onTryAgain()
    }
  }

  private fun onTransactionAndWalletReady(
      transactionAndWallet: Result<Pair<String, List<Transaction>>>) {

    when (transactionAndWallet) {
      Result.Loading -> {
        recyclerTransactions.showLoading()
      }
      is Result.Error -> {
        recyclerTransactions.showError()
      }
      is Result.Success -> {
        adapter =
            TransactionsAdapter(transactionAndWallet.data.first, transactionAndWallet.data.second) {
              transactionsViewModel.onTransactionClicked(adapter!!.items[it], requireActivity())
            }
        recyclerTransactions.adapter = adapter
      }
    }
  }

}