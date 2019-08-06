package com.example.billingpjsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener, View.OnClickListener {

    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBillingClient()
        initComponents()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        println("onPurchasesUpdated: ${billingResult?.responseCode}")
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow
        } else {
            // Handle any other error codes
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        /*For consumable products*/
        if (!purchase.isAcknowledged) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .setDeveloperPayload(purchase.developerPayload)
                .build()
            billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseToken != null) {
                    println("AllowMultiplePurchases success, responseCode: ${billingResult.responseCode}")
                } else {
                    println("Can't allowMultiplePurchases, responseCode: ${billingResult.responseCode}")
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_load_product -> onLoadProductsClicked()
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play by
                // calling the startConnection() method
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    println("BILLING | startConnection | RESULT: OK")
                } else {
                    println("BILLING | startConnection | RESULT: ${billingResult?.responseCode}")
                }
            }
        })
    }

    private fun initComponents() {
        btn_load_product?.setOnClickListener(this)
    }

    private fun onLoadProductsClicked() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetails ->
                if (BillingClient.BillingResponseCode.OK == billingResult.responseCode && !skuDetails.isNullOrEmpty()) {
                    println("querySkuDetailsAsync, responseCode: ${billingResult.responseCode}")
                    setupProductAdapter(skuDetails)
                } else {
                    println("Can't querySkuDetailsAsync, responseCode: ${billingResult.responseCode}")
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }

    private fun setupProductAdapter(products: List<SkuDetails>) {
        productsAdapter = ProductsAdapter(products) {
            /*Enable the purchase of an in-app product*/
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(it)
                .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        }

        rv_products?.apply {
            adapter = productsAdapter
        }
    }

    companion object {
        private val skuList = listOf("Get 5 coins", "Get 10 coins", "Get 20 coins")
    }
}
