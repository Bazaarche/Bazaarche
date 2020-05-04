package com.asfoundation.wallet.ui.bazarchesettings

import android.content.Context
import com.asf.wallet.R
import com.asfoundation.wallet.transactions.Transaction
import com.asfoundation.wallet.transactions.TransactionDetails.Icon.Type.FILE
import com.asfoundation.wallet.transactions.TransactionDetails.Icon.Type.URL
import java.util.*

internal fun Transaction.getAddressText(context: Context, defaultWalletAddress: String): String {
  val isSent = isSent(defaultWalletAddress)

  return if (details != null && type == Transaction.TransactionType.BONUS) {
    context.getString(R.string.transaction_type_bonus)
  } else {
    details?.sourceName ?: if (isSent) {
      to
    } else {
      from
    }
  }
}

internal fun Transaction.getUri(): String? = details?.icon?.run {
  when (type) {
    FILE -> "file:$uri"
    URL -> uri
    null -> null
  }
}

internal fun Transaction.isTransactionTypeShouldBeShown(uri: String?): Boolean =
    type != Transaction.TransactionType.BONUS && uri == null || details.sourceName == null

private fun Transaction.isSent(defaultWalletAddress: String): Boolean =
    from.toLowerCase(Locale.ROOT) == defaultWalletAddress
