package com.example.billingpjsample.billing

import android.app.Activity
import android.util.Log
import androidx.annotation.NonNull
import com.android.billingclient.api.*

class BillingManager(@NonNull activity: Activity, @NonNull updatesListener: BillingUpdatesListener)
    : PurchasesUpdatedListener {

    private val TAG = BillingManager::class.java.simpleName
    private var mBillingClient: BillingClient
    private var mIsServerConnected: Boolean = false
    private val mActivity: Activity = activity
    private lateinit var mTokenToBeConsumed: Set<String>
    private lateinit var mPurchases: MutableList<Purchase>
    private val mBillingUpdatesListener: BillingUpdatesListener = updatesListener
    private var mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED

    init {
        mBillingClient = BillingClient
            .newBuilder(mActivity)
            .setListener(this)
            .build()

        startServiceConnection(Runnable {
            mBillingUpdatesListener.onBillingClientSetupFinished()
            Log.d(TAG, "Setup successful. Querying inventory")

            queryPurchases()
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

    private fun handlePurchase(purchase: Purchase) {

    }

    /**
     * Connect to Google Play Store service
     */
    private fun startServiceConnection(executeOnSuccess: Runnable?) {
        mBillingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {
                mIsServerConnected = false
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                Log.d(TAG, "Setup finished, ResponseCode: ${billingResult?.responseCode}")

                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    mIsServerConnected = true
                    executeOnSuccess?.run()
                } else {
                }
                billingResult?.responseCode?.let { mBillingClientResponseCode = it }
            }
        })
    }

    /**
     * Get SKUs detail
     */
    fun querySkuDetailsAsync(
        @BillingClient.SkuType itemType: String,
        skuList: List<String>,
        @NonNull listener: SkuDetailsResponseListener
    ) {
        val queryRequest = Runnable {
            // Query the purchase async
            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(itemType)

            mBillingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                listener.onSkuDetailsResponse(billingResult, skuDetailsList)
            }
        }
    }

    /**
     * query for in-app product detail
     */
    private fun queryPurchases() {
        val queryToExecute = Runnable {
            // Retrieve information about purchases that a user makes from your app
            val purchaseResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP)

            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS)

                if (subscriptionResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchaseResult.purchasesList.addAll(subscriptionResult.purchasesList)
                } else {
                    Log.e(TAG, "Got an error response trying to query subscription purchases")
                }
            } else if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.i(TAG, "Skipped subscription purchases query since they are not supported")
            } else {
                Log.w(TAG, "queryPurchases() got an error response code: ")
            }

            onQueryPurchasesFinished(purchaseResult)
        }

        executeServiceRequest(queryToExecute)
    }

    private fun areSubscriptionsSupported(): Boolean {
        val responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode
        if (responseCode != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: $responseCode")
        }

        return responseCode == BillingClient.BillingResponseCode.OK
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: Purchase.PurchasesResult) {
        if (!mBillingClient.isReady || result.responseCode != BillingClient.BillingResponseCode.OK) {
            return
        }

        Log.d(TAG, "Query inventory was successful")

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear()
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (mIsServerConnected && mBillingClient.isReady) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            startServiceConnection(runnable)
        }
    }

    companion object {
        private const val BILLING_MANAGER_NOT_INITIALIZED = -1
    }

    interface BillingUpdatesListener {

        fun onBillingClientSetupFinished()

        fun onConsumeFinish(token: String, @BillingClient.BillingResponseCode result: Int)

        fun onPurchasesUpdated(purchases: MutableList<Purchase>)
    }

    interface ServiceConnectedListener {

        fun onServiceConnected(@BillingClient.BillingResponseCode resultCode: Int)
    }
}
