package com.appcoins.wallet.bdsbilling.repository

import com.appcoins.wallet.bdsbilling.repository.entity.*
import com.appcoins.wallet.billing.repository.entity.Product
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.http.*
import java.math.BigDecimal

class RemoteRepository(private val api: BdsApi, private val responseMapper: BdsApiResponseMapper,
                       private val bdsApiSecondary: BdsApiSecondary) {
  companion object {
    private const val SKUS_DETAILS_REQUEST_LIMIT = 50
  }

  internal fun isBillingSupported(packageName: String,
                                  type: BillingSupportedType): Single<Boolean> {
    return api.getPackage(packageName, type.name.toLowerCase())
        .map { true } // If it's not supported it returns an error that is handle in BdsBilling.kt
  }

  internal fun getSkuDetails(packageName: String, skus: List<String>): Single<List<Product>> {
    return requestSkusDetails(packageName, skus).map { responseMapper.map(it) }
  }

  private fun requestSkusDetails(packageName: String,
                                 skus: List<String>): Single<DetailsResponseBody> {
    return if (skus.size <= SKUS_DETAILS_REQUEST_LIMIT) {
      api.getPackages(packageName, skus.joinToString(separator = ","))
    } else {
      Single.zip(
          api.getPackages(packageName,
              skus.take(SKUS_DETAILS_REQUEST_LIMIT).joinToString(separator = ",")),
          requestSkusDetails(packageName, skus.drop(SKUS_DETAILS_REQUEST_LIMIT)),
          BiFunction { firstResponse: DetailsResponseBody, secondResponse: DetailsResponseBody ->
            firstResponse.merge(secondResponse)
          })
    }
  }

  internal fun getSkuPurchase(packageName: String,
                              skuId: String?,
                              walletAddress: String,
                              walletSignature: String): Single<Purchase> {
    return api.getSkuPurchase(packageName, skuId, walletAddress, walletSignature)
  }

  internal fun getSkuTransaction(packageName: String,
                                 skuId: String?,
                                 walletAddress: String,
                                 walletSignature: String): Single<TransactionsResponse> {
    return api.getSkuTransaction(walletAddress, walletSignature, 0, TransactionType.INAPP, 1,
        "latest", false, skuId, packageName)

  }

  internal fun getPurchases(packageName: String,
                            walletAddress: String,
                            walletSignature: String,
                            type: BillingSupportedType): Single<List<Purchase>> {
    return api.getPurchases(packageName, walletAddress, walletSignature,
        type.name.toLowerCase())
        .map { responseMapper.map(it) }
  }

  internal fun consumePurchase(packageName: String,
                               purchaseToken: String,
                               walletAddress: String,
                               walletSignature: String): Single<Boolean> {
    return api.consumePurchase(packageName, purchaseToken, walletAddress, walletSignature,
        Consumed())
        .map { true }
  }

  fun registerAuthorizationProof(origin: String?, type: String, oemWallet: String, id: String?,
                                 gateway: String, walletAddress: String, walletSignature: String,
                                 productName: String?, packageName: String, priceValue: BigDecimal,
                                 developerWallet: String, storeWallet: String,
                                 developerPayload: String?, callback: String?,
                                 orderReference: String?,
                                 referrerUrl: String?): Single<Transaction> {
    return api.createTransaction(gateway, origin, packageName, priceValue.toPlainString(),
        "APPC", productName, type, null, developerWallet, storeWallet, oemWallet, id,
        developerPayload, callback, orderReference, referrerUrl, walletAddress, walletSignature)
  }

  fun registerPaymentProof(paymentId: String, paymentType: String, walletAddress: String,
                           walletSignature: String,
                           paymentProof: String): Completable {
    return api.patchTransaction(paymentType, paymentId, walletAddress, walletSignature,
        paymentProof)
  }

  internal fun getPaymentMethods(value: String?,
                                 currency: String?): Single<List<PaymentMethodEntity>> {
    return api.getPaymentMethods(value, currency)
        .map { responseMapper.map(it) }
  }

  internal fun getPaymentMethodsForType(type: String): Single<List<PaymentMethodEntity>> {
    return api.getPaymentMethods(type = type)
        .map { responseMapper.map(it) }
  }

  fun getAppcoinsTransaction(uid: String, address: String,
                             signedContent: String): Single<Transaction> {
    return api.getAppcoinsTransaction(uid, address, signedContent)
  }

  fun getWallet(packageName: String): Single<GetWalletResponse> {
    return bdsApiSecondary.getWallet(packageName)
  }

  fun transferCredits(toWallet: String, origin: String, type: String, gateway: String,
                      walletAddress: String, signature: String, packageName: String,
                      amount: BigDecimal): Completable {
    return api.createTransaction(gateway, origin, packageName, amount.toPlainString(),
        "APPC", null, type, toWallet, null, null,
        null, null, null, null, null, null,
        walletAddress, signature)
        .toCompletable()

  }

  interface BdsApi {

    @GET("inapp/8.20180518/packages/{packageName}")
    fun getPackage(@Path("packageName") packageName: String, @Query("type")
    type: String): Single<GetPackageResponse>

    @GET("inapp/8.20180518/packages/{packageName}/products?country=IR")
    fun getPackages(@Path("packageName") packageName: String,
                    @Query("names") names: String): Single<DetailsResponseBody>

    @GET("inapp/8.20180518/packages/{packageName}/products/{skuId}/purchase")
    fun getSkuPurchase(@Path("packageName") packageName: String,
                       @Path("skuId") skuId: String?,
                       @Query("wallet.address") walletAddress: String,
                       @Query("wallet.signature") walletSignature: String): Single<Purchase>

    @GET("broker/8.20180518/transactions")
    fun getSkuTransaction(
        @Query("wallet.address") walletAddress: String,
        @Query("wallet.signature") walletSignature: String,
        @Query("cursor") cursor: Long,
        @Query("type") type: TransactionType,
        @Query("limit") limit: Long,
        @Query("sort.name") sort: String,
        @Query("sort.reverse") isReverse: Boolean,
        @Query("product") skuId: String?,
        @Query("domain") packageName: String
    ): Single<TransactionsResponse>

    @GET("broker/8.20180518/transactions/{uId}")
    fun getAppcoinsTransaction(@Path("uId") uId: String,
                               @Query("wallet.address") walletAddress: String,
                               @Query("wallet.signature")
                               walletSignature: String): Single<Transaction>


    @GET("inapp/8.20180518/packages/{packageName}/purchases")
    fun getPurchases(@Path("packageName") packageName: String,
                     @Query("wallet.address") walletAddress: String,
                     @Query("wallet.signature") walletSignature: String,
                     @Query("type") type: String): Single<GetPurchasesResponse>

    @Headers("Content-Type: application/json")
    @PATCH("inapp/8.20180518/packages/{packageName}/purchases/{purchaseId}")
    fun consumePurchase(@Path("packageName") packageName: String,
                        @Path("purchaseId") purchaseToken: String,
                        @Query("wallet.address") walletAddress: String,
                        @Query("wallet.signature") walletSignature: String,
                        @Body data: Consumed): Single<Void>

    @GET("broker/8.20191014/methods")
    fun getPaymentMethods(@Query("price.value") value: String? = null, @Query("price.currency")
    currency: String? = null, @Query("currency.type")
                          type: String? = null): Single<GetMethodsResponse>

    @FormUrlEncoded
    @PATCH("broker/8.20180518/gateways/{gateway}/transactions/{uid}")
    fun patchTransaction(
        @Path("gateway") gateway: String,
        @Path("uid") uid: String, @Query("wallet.address") walletAddress: String,
        @Query("wallet.signature") walletSignature: String, @Field("pay_key")
        paykey: String): Completable

    /**
     * All optional fields should be passed despite possible being null as these are
     * required by some applications to complete the purchase flow
     * @param gateway type of the transaction that is being created;
     * @see com.appcoins.wallet.bdsbilling.repository.entity.Transaction.Status
     * @param origin value from the transaction origin (bds, unity, unknown)
     * @param domain package name of the application
     * @param priceValue amount of the transaction. Only needed in one step payments
     * @param priceCurrency currency of the transaction. Only needed in one step payments
     * @param product name of the product that is being bought
     * @param type name of the payment method being used
     * @param userWallet address of the user wallet
     * @param walletsDeveloper Wallet address of the apps developer
     * @param walletsOem Wallet address of the original equipment manufacturer
     * @param token
     * @param developerPayload Group of details used in some purchases by the application to
     * complete the purchase
     * @param callback url used in some purchases by the application to complete the purchase
     * @param orderReference reference used in some purchases by the application to
     * @param referrerUrl url to validate the transaction
     * @param walletAddress address of the user wallet
     * @param walletSignature signature obtained after signing the wallet
     * complete the purchase
     */
    @FormUrlEncoded
    @POST("broker/8.20180518/gateways/{gateway}/transactions")
    fun createTransaction(@Path("gateway") gateway: String,
                          @Field("origin") origin: String?,
                          @Field("domain") domain: String,
                          @Field("price.value") priceValue: String?,
                          @Field("price.currency") priceCurrency: String,
                          @Field("product") product: String?,
                          @Field("type") type: String,
                          @Field("wallets.user") userWallet: String?,
                          @Field("wallets.developer") walletsDeveloper: String?,
                          @Field("wallets.store") walletsStore: String?,
                          @Field("wallets.oem") walletsOem: String?,
                          @Field("token") token: String?,
                          @Field("metadata") developerPayload: String?,
                          @Field("callback_url") callback: String?,
                          @Field("reference") orderReference: String?,
                          @Field("referrer_url") referrerUrl: String?,
                          @Query("wallet.address") walletAddress: String,
                          @Query("wallet.signature") walletSignature: String): Single<Transaction>
  }

  data class Consumed(val status: String = "CONSUMED")
}
