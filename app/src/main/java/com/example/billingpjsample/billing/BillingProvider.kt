package com.example.billingpjsample.billing

interface BillingProvider {
    fun getBillingManager(): BillingManager
}