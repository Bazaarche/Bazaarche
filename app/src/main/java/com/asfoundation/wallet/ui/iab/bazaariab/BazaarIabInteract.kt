package com.asfoundation.wallet.ui.iab.bazaariab

import android.content.Intent
import android.os.Bundle
import com.appcoins.wallet.bdsbilling.Billing
import com.appcoins.wallet.bdsbilling.WalletService
import com.appcoins.wallet.bdsbilling.repository.entity.Transaction
import com.appcoins.wallet.bdsbilling.repository.entity.Transaction.Status.PROCESSING
import com.appcoins.wallet.bdsbilling.repository.entity.Transaction.Status.COMPLETED
import com.appcoins.wallet.billing.BillingMessagesMapper
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.entity.BazaarchePurchaseInfo
import com.asfoundation.wallet.entity.ProductInfo
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.util.Parameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phelat.poolakey.entity.PurchaseEntity
import com.phelat.poolakey.request.PurchaseRequest
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

    private const val APPC = "APPC"

  }

  override fun getPurchaseRequest(): Single<PurchaseRequest> {
    return walletService.getWalletAddress()
        .map { getProductInfoJson(it) }
        .map {
          PurchaseRequest(it, PURCHASE_REQUEST, transaction.payload)
        }
  }

  override fun getPurchaseInfo(data: Intent, purchaseEntity: PurchaseEntity): Single<BazaarchePurchaseInfo> {

    return Single.just(data.getStringExtra(RESPONSE_PURCHASE_DATA))
        .map { mapToBazaarchePurchaseInfo(purchaseEntity, it) }
  }

  override fun getPurchaseBundle(uid: String): Single<Bundle> {
    return getCompletedTransaction(uid)
        .flatMap { providePurchaseBundle(it.hash) }
  }

  override fun getCancelBundle(): Bundle = billingMessagesMapper.mapCancellation()

  override fun getErrorBundle(): Bundle = billingMessagesMapper.genericError()

  private fun getProductInfoJson(walletAddress: String): String {

    fun getProductInfo(walletAddress: String): ProductInfo {
      return transaction.run {

        val currency : String
        val amount: Double = if (isOneStep()) {
          currency = originalOneStepCurrency
          originalOneStepValue.toDouble()
        } else {
          currency = APPC
          amount().toDouble()
        }

        ProductInfo(skuId, type, walletAddress, domain, amount, callbackUrl,
            orderReference, toAddress(), currency, BuildConfig.VERSION_CODE)
      }
    }

    return gson.toJson(getProductInfo(walletAddress))
  }

  private fun mapToBazaarchePurchaseInfo(purchaseEntity: PurchaseEntity,
                                         purchaseData: String): BazaarchePurchaseInfo {

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

  private fun getCompletedTransaction(uid: String): Single<Transaction> {

    fun throwErrorIfUnexpectedStatus(transaction: Transaction) {
      if (transaction.status != PROCESSING && transaction.status != COMPLETED) {
        error("Not expected status")
      }
    }

    return Observable.interval(0, 5, TimeUnit.SECONDS, scheduler)
        .switchMapSingle {
          billing.getAppcoinsTransaction(uid, scheduler)
        }
        .doOnNext(::throwErrorIfUnexpectedStatus)
        .takeUntil {
          it.status == COMPLETED
        }
        .singleOrError()
  }

  private fun providePurchaseBundle(transactionHash: String?): Single<Bundle> {
    return transaction.run {
      if (isOneStep()) {
        Single.just(billingMessagesMapper.successBundle(transactionHash))
      } else {
        billing.getSkuPurchase(domain, skuId, scheduler)
            .map {
              billingMessagesMapper.mapPurchase(it, orderReference)
            }
      }
    }
  }

  private fun TransactionBuilder.isOneStep() =
      type.equals(Parameters.PAYMENT_TYPE_INAPP_UNMANAGED, false)

}