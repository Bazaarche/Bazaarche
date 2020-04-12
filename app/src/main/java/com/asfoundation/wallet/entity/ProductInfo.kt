package com.asfoundation.wallet.entity

import com.google.gson.annotations.SerializedName

data class ProductInfo(val product: String,
                       val type: String,
                       @SerializedName("wallet_address") val walletAddress: String,
                       val domain: String,
                       val amount: Double,
                       @SerializedName("callback_url") val callbackUrl: String?,
                       val reference: String?,
                       @SerializedName("wallets_developer") val walletsDeveloper: String,
                       val currency: String,
                       val clientVersion: Int)