package com.example.billingpjsample.billing

interface BillingProvider {
    fun getBillingManager(): BillingManager

    fun isPremiumPurchase(): Boolean

    fun isGoldMonthlySubscribed(): Boolean

    fun isTankFull(): Boolean

    fun isGoldYearlySubscribed(): Boolean
}
