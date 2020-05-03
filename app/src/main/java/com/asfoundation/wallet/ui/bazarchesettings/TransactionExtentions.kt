package com.asfoundation.wallet.ui.bazarchesettings

import android.content.Context
import com.asf.wallet.R
import com.asfoundation.wallet.transactions.Transaction
import com.asfoundation.wallet.transactions.TransactionDetails
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

internal fun Transaction.getUri(): String? {
  return when (details?.icon?.type) {
    TransactionDetails.Icon.Type.FILE -> "file:" + details.icon.uri
    TransactionDetails.Icon.Type.URL -> details.icon.uri
    else -> null
  }
}

private fun Transaction.isSent(defaultWalletAddress: String): Boolean =
    from.toLowerCase(Locale.ROOT) == defaultWalletAddress
