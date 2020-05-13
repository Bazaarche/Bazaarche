package com.asfoundation.wallet.ui.bazarchesettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.asfoundation.wallet.entity.Result
import com.asfoundation.wallet.interact.TransactionViewInteract
import com.asfoundation.wallet.transactions.Transaction
import com.asfoundation.wallet.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TransactionsViewModel(private val transactionViewInteract: TransactionViewInteract) :
    BaseViewModel() {

  private val _transactionsAndWallet = MutableLiveData<Result<Pair<String, List<Transaction>>>>()
  val transactionsAndWallet: LiveData<Result<Pair<String, List<Transaction>>>> =
      _transactionsAndWallet

  init {
    fetchTransactionsAndWalletData()
  }

  fun onTryAgain() {
    fetchTransactionsAndWalletData()
  }

  private fun fetchTransactionsAndWalletData() {

    _transactionsAndWallet.value = Result.Loading

    disposable = transactionViewInteract.findWallet()
        .flatMapObservable { wallet ->
          transactionViewInteract.fetchTransactions(wallet)
              .map { wallet.address to it }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ _transactionsAndWallet.value = Result.Success(it) },
            { _transactionsAndWallet.value = Result.Error(it) })
  }

}

class TransactionsViewModelFactory @Inject constructor(
    private val transactionViewInteract: TransactionViewInteract) :
    BaseViewModelFactory<TransactionsViewModel>(TransactionsViewModel::class.java,
        { TransactionsViewModel(transactionViewInteract) })