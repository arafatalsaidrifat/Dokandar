package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Entity(tableName = "production_nodes")
data class ProductionNode(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val banglaName: String = "",
    val lineType: String = "",
    val status: String = "OPTIMAL",
    val efficiencyPercent: Int = 95,
    val dailyTargetUnits: Int = 1000,
    val completedUnits: Int = 920,
    val carbonIndexKg: Float = 38.0f,
    val backupNodeId: String? = null
)

@IgnoreExtraProperties
@Entity(tableName = "inspection_records")
data class InspectionRecord(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val targetType: String = "Denim Inseam Stitch",
    val hasDefect: Boolean = false,
    val defectLabel: String = "Verified",
    val banglaDefectLabel: String = "ভেরিফাইড",
    val confidenceScore: Float = 0.99f,
    val boxLeft: Float = 0.2f,
    val boxTop: Float = 0.3f,
    val boxRight: Float = 0.7f,
    val boxBottom: Float = 0.7f,
    val severity: String = "NORMAL",
    val correctiveAction: String = "Pass to packaging",
    val banglaCorrectiveAction: String = "প্যাকেজিংয়ের জন্য অনুমোদিত",
    val affectedStation: String = "Station 1A",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toFirestoreMap(uid: String): Map<String, Any> {
        return mapOf(
            "userId" to uid,
            "targetType" to targetType.take(200),
            "hasDefect" to hasDefect,
            "defectLabel" to defectLabel.take(300),
            "banglaDefectLabel" to banglaDefectLabel.take(300),
            "confidenceScore" to confidenceScore.toDouble().coerceIn(0.0, 1.0),
            "severity" to (if (severity in listOf("NORMAL", "MINOR", "HIGH", "CRITICAL")) severity else "NORMAL"),
            "correctiveAction" to correctiveAction.take(1000),
            "banglaCorrectiveAction" to banglaCorrectiveAction.take(1000),
            "affectedStation" to affectedStation.take(120),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
@Entity(tableName = "voice_actions")
data class VoiceAction(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val transcript: String = "",
    val language: String = "en",
    val intent: String = "CREATE_INVOICE",
    val structuredJson: String = "{}",
    val status: String = "EXECUTED",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toFirestoreMap(uid: String): Map<String, Any> {
        return mapOf(
            "userId" to uid,
            "transcript" to transcript.take(500).ifBlank { "Voice command" },
            "language" to (if (language in listOf("bn", "en")) language else "en"),
            "intent" to intent.take(80).ifBlank { "LEDGER_UPDATE" },
            "structuredJson" to structuredJson.take(4000).ifBlank { "{}" },
            "status" to (if (status in listOf("EXECUTED", "VERIFIED")) status else "EXECUTED"),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

@IgnoreExtraProperties
@Entity(tableName = "payment_agreements")
data class PaymentAgreement(
    @PrimaryKey
    @DocumentId
    val agreementId: String = "",
    val userId: String = "",
    val agreementCode: String = "",
    val customerMsisdn: String = "01723-994120",
    val payerReference: String = "Apex Garments Ltd",
    val mode: String = "0011",
    val status: String = "BOUND",
    val recurringCycle: String = "THIRTY_DAYS",
    val amountBdt: Double = 25000.0,
    val boundTimestamp: Long = System.currentTimeMillis(),
    val nextBillingDate: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toFirestoreMap(uid: String): Map<String, Any> {
        val code = agreementCode.ifBlank { agreementId.ifBlank { "AGR-BKASH-0011" } }
        return mapOf(
            "userId" to uid,
            "agreementCode" to code.take(100),
            "customerMsisdn" to customerMsisdn.take(40).ifBlank { "01723-994120" },
            "payerReference" to payerReference.take(160).ifBlank { "Dhaka Apparels Hub" },
            "mode" to (if (mode in listOf("0011", "STRIPE_USD")) mode else "0011"),
            "status" to (if (status in listOf("BOUND", "ACTIVE", "PAUSED")) status else "BOUND"),
            "recurringCycle" to (if (recurringCycle in listOf("THIRTY_DAYS", "MONTHLY")) recurringCycle else "THIRTY_DAYS"),
            "amountBdt" to amountBdt.coerceIn(0.0, 100000000.0),
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }
    }
}

data class TelemetryKpi(
    val activeFactories: Int = 18,
    val activeDokans: Int = 84,
    val defectReductionPercent: Double = 34.2,
    val gmvTotalBdt: Double = 12450000.0,
    val monthlyRecurringRevenueUsd: Double = 14200.0,
    val netRevenueRetentionPercent: Double = 128.5,
    val ltvCacRatio: Double = 4.6,
    val scope3CarbonSavedKg: Double = 8920.0,
    val ideaPreSeedTrancheStatus: String = "TRANCHE_1_VERIFIED",
    val sblGovernanceScore: Int = 96
)
