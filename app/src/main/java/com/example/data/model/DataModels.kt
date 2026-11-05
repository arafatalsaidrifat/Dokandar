package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production_nodes")
data class ProductionNode(
    @PrimaryKey val id: String,
    val name: String,
    val banglaName: String,
    val lineType: String, // e.g. "Sewing", "Cutting", "Finishing", "PCB Assembly"
    val status: String,   // "OPTIMAL", "DEGRADED", "BOTTLENECK_REROUTED"
    val efficiencyPercent: Int,
    val dailyTargetUnits: Int,
    val completedUnits: Int,
    val carbonIndexKg: Float,
    val backupNodeId: String? = null
)

@Entity(tableName = "inspection_records")
data class InspectionRecord(
    @PrimaryKey val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val targetType: String,      // "Garment Stitching", "Denim Seam", "PCB Solder"
    val hasDefect: Boolean,
    val defectLabel: String,
    val banglaDefectLabel: String,
    val confidenceScore: Float,
    // Normalized bounding box 0.0 - 1.0 (left, top, right, bottom)
    val boxLeft: Float,
    val boxTop: Float,
    val boxRight: Float,
    val boxBottom: Float,
    val severity: String,        // "NORMAL", "MINOR", "CRITICAL"
    val correctiveAction: String,
    val banglaCorrectiveAction: String,
    val affectedStation: String
)

@Entity(tableName = "voice_actions")
data class VoiceAction(
    @PrimaryKey val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val transcript: String,
    val language: String,       // "bn" or "en"
    val intent: String,         // "CREATE_INVOICE", "RESTOCK_SUPPLIER", "REROUTE_STATION", "LEDGER_UPDATE"
    val structuredJson: String,
    val status: String          // "EXECUTED", "VERIFIED"
)

@Entity(tableName = "payment_agreements")
data class PaymentAgreement(
    @PrimaryKey val agreementId: String,
    val customerMsisdn: String,
    val payerReference: String,
    val mode: String = "0011",   // CREATE_AGREEMENT
    val status: String,          // "BOUND", "ACTIVE", "PAUSED"
    val recurringCycle: String = "THIRTY_DAYS",
    val amountBdt: Double,
    val boundTimestamp: Long = System.currentTimeMillis(),
    val nextBillingDate: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
)

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
