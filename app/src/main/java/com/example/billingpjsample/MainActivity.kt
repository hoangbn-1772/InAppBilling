package com.example.billingpjsample

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.example.billingpjsample.billing.BillingManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), View.OnClickListener, BillingManager.BillingUpdatesListener,
    BillingManager.ServiceConnectedListener {

    private val TAG = MainActivity::class.java.simpleName
    private var mBillingManager: BillingManager? = null
    private val skuAdapter by lazy {
        SkuAdapter(ArrayList(), this@MainActivity::onProductClicked, this@MainActivity::onLoadRewardedProduct)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    override fun onStart() {
        super.onStart()
        mBillingManager?.getPurchased()
    }

    override fun onResume() {
        super.onResume()
        mBillingManager?.getPurchased()
        if (mBillingManager != null &&
            mBillingManager?.getBillingClientResponseCode() == BillingClient.BillingResponseCode.OK) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBillingManager != null) {
            mBillingManager?.destroy()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_drive -> onDriveButtonClicked()
            R.id.btn_purchase -> onPurchaseButtonClicked()
        }
    }

    override fun onServiceConnected(resultCode: Int) {
        Toast.makeText(this, "Connected service $resultCode", Toast.LENGTH_SHORT).show()
    }

    override fun onConsumeFinish(token: String, result: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPurchasesUpdated(purchases: MutableList<Purchase>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResultPurchased(purchasesResult: Purchase.PurchasesResult?) {

    }

    override fun onResultSkuDetail(billingResult: BillingResult?, skuDetails: List<SkuDetails>?) {
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && !skuDetails.isNullOrEmpty()) {
            skuAdapter.updateProducts(skuDetails)
        } else {
            Log.d(TAG, billingResult?.debugMessage)
        }
    }

    private fun initComponents() {
        btn_drive?.setOnClickListener(this)
        btn_purchase?.setOnClickListener(this)
        rv_products?.apply {
            adapter = skuAdapter
        }

        mBillingManager = BillingManager(this, this, this)
    }

    private fun onPurchaseButtonClicked() {
        mBillingManager?.querySkuDetailsAsync(SKU)
    }

    private fun onDriveButtonClicked() {
    }

    @UiThread
    fun alert(@StringRes messageId: Int, @Nullable optionalParam: Any?) {
        if (Looper.getMainLooper().thread != Thread.currentThread()) {
            throw RuntimeException("Dialog could be shown only from the main thread")
        }

        val bld = AlertDialog.Builder(this)
        bld.setNeutralButton("OK", null)

        if (optionalParam == null) {
            bld.setMessage(messageId)
        } else {
            bld.setMessage(resources.getString(messageId, optionalParam))
        }
        bld.create().show()
    }

    fun showRefreshedUi() {
    }

    /**
     * Sets image resource and also adds a tag to be able to verify that image is correct in tests
     */
    private fun setImageResourceWithTestTag(imageView: AppCompatImageView, @DrawableRes resId: Int) {
        imageView.setImageResource(resId)
        imageView.tag = resId
    }

    private fun onProductClicked(skuDetails: SkuDetails) {
        // Init purchase flow
        mBillingManager?.initiatePurchaseFlow(skuDetails)
    }

    /*Load video ads*/
    private fun onLoadRewardedProduct(skuDetails: SkuDetails) {
        mBillingManager?.loadVideoAds(skuDetails)
    }

    companion object {

        private val SKU = HashMap<String, List<String>>().apply {
            put(BillingClient.SkuType.INAPP, listOf(
                "android.test.purchased", "android.test.purchased", "android.test.item_unavailable",
                "android.test.canceled", "android.test.reward", "android.test.reward"
            ))
            put(BillingClient.SkuType.SUBS, listOf("android.test.purchased", "android.test.purchased"))
        }

        private const val DIALOG_TAG = "dialog"
    }
}
