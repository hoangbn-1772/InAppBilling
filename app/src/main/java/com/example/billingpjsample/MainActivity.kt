package com.example.billingpjsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.billingclient.api.*
import com.example.billingpjsample.billing.BillingManager
import com.example.billingpjsample.billing.BillingProvider
import com.example.billingpjsample.skulist.AcquireFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), BillingProvider, View.OnClickListener, BillingManager.BillingUpdatesListener {

    private lateinit var mBillingManager: BillingManager
    private var mAcquireFragment: AcquireFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_drive -> {
            }
            R.id.btn_purchase -> {
            }
        }
    }

    override fun getBillingManager(): BillingManager {
        return mBillingManager
    }

    override fun onBillingClientSetupFinished() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConsumeFinish(token: String, result: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPurchasesUpdated(purchases: MutableList<Purchase>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initComponents() {
        btn_drive?.setOnClickListener(this)
        btn_purchase?.setOnClickListener(this)

        mBillingManager = BillingManager(this, this)
    }

    private fun onPurchaseButtonClicked(arg0: View) {
        if (mAcquireFragment == null) {
            mAcquireFragment = AcquireFragment()
        }

        if (!isAcquireFragmentShown()) {
            mAcquireFragment?.show(supportFragmentManager, DIALOG_TAG)
        }
    }

    private fun isAcquireFragmentShown(): Boolean {
        return mAcquireFragment != null
    }

    companion object {

        private val SKUS = HashMap<String, List<String>>().apply {
            put(BillingClient.SkuType.INAPP, listOf("gas", "premium"))
            put(BillingClient.SkuType.SUBS, listOf("gold_monthly", "gold_yearly"))
        }

        private const val DIALOG_TAG = "dialog"
    }
}
