package com.example.payments

import com.example.data.model.PaymentAgreement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID

// bKash Tokenized Checkout API (v1.2.0-beta) Single-Flight Token Manager
class BkashTokenManager {
    private val mutex = Mutex()
    private var cachedIdToken: String? = null
    private var tokenExpiryTimestamp: Long = 0L

    suspend fun getValidIdToken(): String = mutex.withLock {
        val now = System.currentTimeMillis()
        // Refresh 60 seconds before 3600-second expiration to avoid rate limits
        if (cachedIdToken != null && now < (tokenExpiryTimestamp - 60_000L)) {
            return cachedIdToken!!
        }

        // Simulate single-flight server grant call to /tokenized/checkout/token/grant
        delay(250)
        val newToken = "BKASH_ID_TOKEN_${UUID.randomUUID().toString().take(12)}"
        cachedIdToken = newToken
        tokenExpiryTimestamp = now + (3600_000L) // 1 hour validity
        newToken
    }
}

// Complete Dual-Market Payment Engine
class DualMarketPaymentEngine {
    private val tokenManager = BkashTokenManager()

    sealed class BkashBindingStep {
        object Idle : BkashBindingStep()
        data class WalletInput(val draftMsisdn: String) : BkashBindingStep()
        data class OtpVerification(val msisdn: String, val otpInput: String) : BkashBindingStep()
        data class PinConfirmation(val msisdn: String, val pinInput: String) : BkashBindingStep()
        data class AgreementBound(val agreement: PaymentAgreement) : BkashBindingStep()
        data class Error(val message: String) : BkashBindingStep()
    }

    // bKash Tokenized Agreement Creation (mode "0011")
    suspend fun initiateAgreement(msisdn: String, customerName: String, amountBdt: Double): BkashBindingStep = withContext(Dispatchers.IO) {
        tokenManager.getValidIdToken()
        delay(400)
        BkashBindingStep.OtpVerification(msisdn, "")
    }

    suspend fun verifyOtpAndPromptPin(msisdn: String, otp: String): BkashBindingStep = withContext(Dispatchers.IO) {
        delay(300)
        if (otp.length == 6 || otp == "123456") {
            BkashBindingStep.PinConfirmation(msisdn, "")
        } else {
            BkashBindingStep.Error("Invalid 6-digit OTP received from bKash SMS gateway.")
        }
    }

    suspend fun executeAgreement(
        msisdn: String,
        pin: String,
        customerName: String,
        amountBdt: Double
    ): PaymentAgreement = withContext(Dispatchers.IO) {
        tokenManager.getValidIdToken()
        delay(500)
        val agreementId = "AGR-${UUID.randomUUID().toString().take(10).uppercase()}"
        PaymentAgreement(
            agreementId = agreementId,
            customerMsisdn = msisdn,
            payerReference = customerName.ifBlank { "Dhaka Apparels Hub" },
            mode = "0011",
            status = "BOUND",
            recurringCycle = "THIRTY_DAYS",
            amountBdt = amountBdt,
            boundTimestamp = System.currentTimeMillis(),
            nextBillingDate = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
        )
    }

    // Recurring Auto-Debit execution using stored agreementId without OTP
    suspend fun executeRecurringAutoPay(agreementId: String, amountBdt: Double): Boolean = withContext(Dispatchers.IO) {
        tokenManager.getValidIdToken()
        delay(400)
        true
    }

    // Stripe Global Enterprise Checkout Simulator
    data class StripeSubscription(
        val planName: String,
        val tierPriceUsd: Double,
        val sharedSavingsRatePercent: Int,
        val invoiceCadence: String,
        val globalBuyerJurisdiction: String,
        val paymentStatus: String
    )

    fun createStripePlan(planType: String, enterpriseBuyer: String): StripeSubscription {
        return when (planType) {
            "GLOBAL_ENTERPRISE" -> StripeSubscription(
                planName = "Auralis Enterprise Global Grid",
                tierPriceUsd = 1899.0,
                sharedSavingsRatePercent = 10,
                invoiceCadence = "Monthly Recurring + 10% Utility/Defect Shared Savings",
                globalBuyerJurisdiction = "EU (Germany / Netherlands) & US Retail",
                paymentStatus = "ACTIVE_STRIPE_INTENT"
            )
            else -> StripeSubscription(
                planName = "Auralis Supplier Pro",
                tierPriceUsd = 499.0,
                sharedSavingsRatePercent = 10,
                invoiceCadence = "Monthly SaaS",
                globalBuyerJurisdiction = "Southeast Asia & South Asia",
                paymentStatus = "ACTIVE_STRIPE_INTENT"
            )
        }
    }
}
