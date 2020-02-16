package com.asfoundation.wallet.entity

import com.google.gson.annotations.SerializedName

data class Payload(val product: String,
                   val type: String,
                   val developerPayload: String?,
                   @SerializedName("wallet_address") val walletAddress: String,
                   val domain: String,
                   val amount: Double,
                   @SerializedName("callback_url") val callbackUrl: String?,
                   val reference: String?,
                   @SerializedName("wallets_developer") val walletsDeveloper: String,
                   @SerializedName("wallets_oem") val walletsOem: String)