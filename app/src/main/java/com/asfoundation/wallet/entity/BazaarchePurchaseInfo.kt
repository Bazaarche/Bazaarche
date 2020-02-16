package com.asfoundation.wallet.entity

import com.phelat.poolakey.entity.PurchaseState

//TODO: All of parameters except uid and domain is useless. but I think will be used in the future. Remove if not used
data class BazaarchePurchaseInfo(val orderId: String,
                                 val purchaseToken: String,
                                 val payload: String,
                                 val packageName: String,
                                 val purchaseState: PurchaseState,
                                 val purchaseTime: Long,
                                 val skuId: String,
                                 val domain: String,
                                 val uid: String)
