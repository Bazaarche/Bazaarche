package com.asfoundation.wallet.ui.iab.bazaariab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.asfoundation.wallet.entity.TransactionBuilder
import io.reactivex.Scheduler
import javax.inject.Inject

class BazaarIabViewModelFactory @Inject constructor(private val transaction: TransactionBuilder,
                                                    private val bazaarIabInteract: BazaarIabInteract,
                                                    private val scheduler: Scheduler) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {

    if (modelClass.isAssignableFrom(BazaarIabViewModel::class.java)) {

      @Suppress("UNCHECKED_CAST")
      return BazaarIabViewModel(transaction = transaction,
          bazaarIabInteract = bazaarIabInteract,
          scheduler = scheduler) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}