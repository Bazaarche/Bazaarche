package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import android.os.Bundle
import com.appcoins.wallet.bdsbilling.Billing
import com.appcoins.wallet.bdsbilling.WalletService
import com.appcoins.wallet.bdsbilling.repository.entity.Transaction
import com.appcoins.wallet.billing.BillingMessagesMapper
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.entity.BazaarchePurchaseInfo
import com.asfoundation.wallet.entity.Payload
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.ui.iab.bazaariab.Status.Companion.statusMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phelat.poolakey.entity.PurchaseEntity
import com.phelat.poolakey.request.PurchaseRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BazaarIabInteract @Inject constructor(private val transaction: TransactionBuilder,
                                            private val walletService: WalletService,
                                            private val billing: Billing,
                                            private val billingMessagesMapper: BillingMessagesMapper,
                                            private val gson: Gson,
                                            private val scheduler: Scheduler) : BazaarIabUseCases {

  companion object {

    private const val RESPONSE_PURCHASE_DATA = "INAPP_PURCHASE_DATA"

    private const val PURCHASE_REQUEST = 10001

    private const val DOMAIN = "dealer"
    private const val UID = "uid"

  }

  override fun getPurchaseRequest(): Single<PurchaseRequest> {
    return walletService.getWalletAddress()
        .map { createPayload(it, transaction) }
        .map {
          PurchaseRequest(
              productId = providePurchaseId(),
              requestCode = PURCHASE_REQUEST,
              payload = it)
        }
  }

  override fun getPurchaseInfo(data: Intent, purchaseEntity: PurchaseEntity): Single<BazaarchePurchaseInfo> {

    return Single.just(data.getStringExtra(RESPONSE_PURCHASE_DATA))
        .map { mapper(purchaseEntity, it) }
  }


  override fun waitTransactionCompletion(uid: String): Completable {

    return Observable.interval(0, 5, TimeUnit.SECONDS, scheduler)
        .switchMapSingle {
          billing.getAppcoinsTransaction(uid, scheduler)
        }
        .map(::statusMapper)
        .takeUntil { status ->
          status != Status.PROCESSING
        }
        .ignoreElements()
  }


  override fun getPurchase(domain: String, sku: String): Single<PurchaseState> {
    return billing.getSkuPurchase(domain, sku, scheduler)
        .map { billingMessagesMapper.mapPurchase(it, transaction.orderReference) }
        .map { PurchaseState.Purchased(it) }
  }

  override fun getCancelBundle(): Bundle = billingMessagesMapper.mapCancellation()

  override fun getErrorBundle(): Bundle = billingMessagesMapper.genericError()


  private fun providePurchaseId() = "${transaction.domain}#${transaction.skuId}"

  private fun createPayload(walletAddress: String, transaction: TransactionBuilder): String {
    return transaction.run {

      val payload = Payload(skuId, type, payload, walletAddress, domain, amount().toDouble(), callbackUrl,
          orderReference, toAddress(), BuildConfig.DEFAULT_OEM_ADDRESS)

      gson.toJson(payload)
    }
  }

  private fun mapper(purchaseEntity: PurchaseEntity, purchaseData: String): BazaarchePurchaseInfo {

    val type = object : TypeToken<Map<String, String>>() {}.type
    val purchaseDataMap = gson.fromJson<Map<String, String>>(purchaseData, type)

    val purchaseInfo = purchaseEntity.purchaseInfo

    return purchaseInfo.run {

      BazaarchePurchaseInfo(
          orderId = orderId,
          purchaseToken = purchaseToken,
          payload = payload,
          packageName = packageName,
          purchaseState = purchaseState,
          purchaseTime = purchaseTime,
          skuId = productId,
          domain = purchaseDataMap.getValue(DOMAIN),
          uid = purchaseDataMap.getValue(UID)
      )
    }

  }


}


private enum class Status {
  PROCESSING,
  COMPLETED;

  companion object {

    fun statusMapper(transaction: Transaction): Status {
      return when (transaction.status) {
        Transaction.Status.PROCESSING -> PROCESSING
        Transaction.Status.COMPLETED -> COMPLETED
        else -> error("Not expected status")
      }
    }
  }

}
