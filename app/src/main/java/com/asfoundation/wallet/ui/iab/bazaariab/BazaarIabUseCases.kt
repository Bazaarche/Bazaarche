package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import com.asfoundation.wallet.entity.BazaarchePurchaseInfo
import com.phelat.poolakey.entity.PurchaseEntity
import com.phelat.poolakey.request.PurchaseRequest
import io.reactivex.Completable
import io.reactivex.Single

interface BazaarIabUseCases {

  fun getPurchaseRequest(): Single<PurchaseRequest>

  fun getPurchaseInfo(data: Intent, purchaseEntity: PurchaseEntity): Single<BazaarchePurchaseInfo>

  fun waitTransactionCompletion(uid: String): Completable

  fun getPurchase(domain: String, sku: String): Single<PurchaseState>

  fun getCanceledError(): PurchaseState.Error

  fun getGenericError(): PurchaseState.Error
}