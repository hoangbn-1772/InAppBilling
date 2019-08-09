package com.example.billingpjsample

import android.content.Context
import com.android.billingclient.api.Purchase
import com.example.billingpjsample.billing.BillingManager

class MainController(activity: MainActivity) {

    private var mUpdateListener: UpdateListener? = null
    private var mActivity: MainActivity? = null

    // Tracks if we currently own subs SKUs
    private var mGoldMonthly = false
    private var mGoldYearly = false

    // Tracks if we currently own a premium car
    private var mIsPremium = false

    // Current amount of gas in tank, in units
    private var mTank: Int = -1

    init {
        mUpdateListener = UpdateListener()
        mActivity = activity
        loadData()
    }

    fun useGas() {
        mTank--
        saveData()
    }

    fun getUpdateListener(): UpdateListener? = mUpdateListener

    fun isTankEmpty(): Boolean = mTank <= 0

    fun isTankFull(): Boolean = mTank >= TANK_MAX

    fun isPremiumPurchased(): Boolean = mIsPremium

    fun isGoldMonthlySubscribed() = mGoldMonthly

    fun isGoldYearlySubscribed() = mGoldYearly

    fun getTankResId(): Int = if (mTank >= TANK_RES_IDS.size) TANK_RES_IDS.size - 1 else mTank


    private fun loadData() {
        val sp = mActivity?.getPreferences(Context.MODE_PRIVATE)
        mTank = sp?.getInt("tank", 2) ?: 2
    }

    private fun saveData() {
        val spe = mActivity?.getPreferences(Context.MODE_PRIVATE)?.edit()?.apply {
            putInt("tank", mTank)
            apply()
        }
    }

    companion object {

        private val TANK_RES_IDS = arrayListOf(
            R.drawable.gas0, R.drawable.gas1, R.drawable.gas2, R.drawable.gas3, R.drawable.gas4)

        private const val TANK_MAX = 4
    }

    class UpdateListener : BillingManager.BillingUpdatesListener {

        override fun onBillingClientSetupFinished() {

        }

        override fun onConsumeFinish(token: String, result: Int) {

        }

        override fun onPurchasesUpdated(purchases: MutableList<Purchase>) {

        }
    }
}