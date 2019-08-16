package com.example.billingpjsample.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.android.billingclient.api.*

class BillingManager(
    @NonNull activity: Activity,
    @NonNull updatesListener: BillingUpdatesListener,
    @NonNull serviceConnectedListener: ServiceConnectedListener
) : PurchasesUpdatedListener {

    private val TAG = BillingManager::class.java.simpleName
    private var mBillingClient: BillingClient? = null
    private var mIsServerConnected: Boolean = false
    private val mActivity: Activity = activity
    private var mPurchases: MutableList<Purchase>? = null
    private val mBillingUpdatesListener: BillingUpdatesListener = updatesListener
    private var mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
    private var mServiceConnectedListener: ServiceConnectedListener = serviceConnectedListener

    init {
        mBillingClient = BillingClient
            .newBuilder(mActivity)
            .setListener(this)
            .setChildDirected(BillingClient.ChildDirected.NOT_CHILD_DIRECTED)
            .enablePendingPurchases()
            .build()

        startServiceConnection(Runnable {
            Log.d(TAG, "Setup successful. Querying inventory")
        })
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.i(TAG, "onPurchasesUpdated - user cancelled the purchase flow - skipping")
            }

            else -> {
                Log.w(TAG, "onPurchasesUpdated - got unknown resultCode")
            }
        }
    }

    fun getBillingClientResponseCode(): Int = mBillingClientResponseCode

    private fun handlePurchase(purchase: Purchase) {

        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                if (purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    mBillingClient?.acknowledgePurchase(acknowledgePurchaseParams) {
                        Log.d(TAG, it.responseCode.toString())
                    }
                } else {
                    val consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .setDeveloperPayload(purchase.developerPayload)
                        .build()

                    mBillingClient?.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                        Log.d(TAG, "${billingResult.responseCode} + $purchaseToken")
                    }
                }
            }

            Purchase.PurchaseState.PENDING -> {
                Log.d(TAG, "PurchaseState.PENDING")
            }
        }
    }

    /**
     * Connect to Google Play Store service
     */
    private fun startServiceConnection(executeOnSuccess: Runnable?) {
        mBillingClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {
                mIsServerConnected = false
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                Log.d(TAG, "Setup finished, ResponseCode: ${billingResult?.responseCode}")

                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    mIsServerConnected = true
                    mServiceConnectedListener.onServiceConnected(billingResult.responseCode)
                    executeOnSuccess?.run()
                } else {
                }
                billingResult?.responseCode?.let { mBillingClientResponseCode = it }
            }
        })
    }

    /**
     * query for in-app product detail
     */
    fun querySkuDetailsAsync(sku: HashMap<String, List<String>>) {
        val queryRequest = Runnable {
            val skuList = ArrayList<String>()
            sku[BillingClient.SkuType.INAPP]?.get(0)?.let { skuList.add(it) }
            sku[BillingClient.SkuType.INAPP]?.get(1)?.let { skuList.add(it) }
            sku[BillingClient.SkuType.INAPP]?.get(2)?.let { skuList.add(it) }
            sku[BillingClient.SkuType.INAPP]?.get(3)?.let { skuList.add(it) }
            sku[BillingClient.SkuType.INAPP]?.get(4)?.let { skuList.add(it) }
            sku[BillingClient.SkuType.INAPP]?.get(5)?.let { skuList.add(it) }

            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)

            mBillingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                mBillingUpdatesListener.onResultSkuDetail(billingResult, skuDetailsList)
            }
        }
        if (mBillingClient != null && mBillingClient?.isReady!!) {
            queryRequest.run()
        }
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (mIsServerConnected && mBillingClient?.isReady!!) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            startServiceConnection(runnable)
        }
    }

    /*Create Purchase Flow*/
    fun initiatePurchaseFlow(skuDetails: SkuDetails) {
        val purchaseFlowRequest = Runnable {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()

            val responseCode = mBillingClient?.launchBillingFlow(mActivity, flowParams)
        }

        executeServiceRequest(purchaseFlowRequest)
    }

    /**
     * To retrieve information about purchases that a user makes from your app
     */
    fun getPurchased() {
        val purchasesResult = mBillingClient?.queryPurchases(BillingClient.SkuType.INAPP)
        mBillingUpdatesListener.onResultPurchased(purchasesResult)
    }

    /**
     * Rewarded product
     */
    fun loadVideoAds(skuDetails: SkuDetails) {
        val params = RewardLoadParams.Builder()
            .setSkuDetails(skuDetails)
            .build()

        mBillingClient?.loadRewardedSku(params) {
            if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Load video successful")
                initiatePurchaseFlow(skuDetails)
            }
        }
    }

    fun destroy() {
        if (mBillingClient != null && mBillingClient?.isReady!!) {
            mBillingClient?.endConnection()
            mBillingClient = null
        }
    }

    companion object {
        private const val BILLING_MANAGER_NOT_INITIALIZED = -1
    }

    interface BillingUpdatesListener {

        fun onConsumeFinish(token: String, @BillingClient.BillingResponseCode result: Int)

        fun onPurchasesUpdated(purchases: MutableList<Purchase>)

        fun onResultSkuDetail(billingResult: BillingResult?, skuDetails: List<SkuDetails>?)

        fun onResultPurchased(purchasesResult: Purchase.PurchasesResult?)
    }

    interface ServiceConnectedListener {

        fun onServiceConnected(@BillingClient.BillingResponseCode resultCode: Int)
    }
}
