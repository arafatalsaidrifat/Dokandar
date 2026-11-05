package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.model.InspectionRecord
import com.example.data.model.PaymentAgreement
import com.example.data.model.ProductionNode
import com.example.data.model.TelemetryKpi
import com.example.data.model.VoiceAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuralisRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "auralis_os_db"
    ).fallbackToDestructiveMigration(dropAllTables = true).build()

    private val nodeDao = db.productionNodeDao()
    private val inspectionDao = db.inspectionDao()
    private val voiceDao = db.voiceActionDao()
    private val paymentDao = db.paymentAgreementDao()

    private val _telemetry = MutableStateFlow(TelemetryKpi())
    val telemetry: Flow<TelemetryKpi> = _telemetry.asStateFlow()

    val nodes: Flow<List<ProductionNode>> = nodeDao.getAllNodes()
    val inspections: Flow<List<InspectionRecord>> = inspectionDao.getRecentInspections()
    val voiceActions: Flow<List<VoiceAction>> = voiceDao.getRecentActions()
    val agreements: Flow<List<PaymentAgreement>> = paymentDao.getAllAgreements()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val defaultNodes = listOf(
            ProductionNode(
                id = "NODE-01",
                name = "Line 1: High-Speed Overlock Stitching",
                banglaName = "লাইন ১: হাই-স্পিড ওভারলক স্টিচিং",
                lineType = "Textile Assembly",
                status = "OPTIMAL",
                efficiencyPercent = 94,
                dailyTargetUnits = 1200,
                completedUnits = 980,
                carbonIndexKg = 42.5f,
                backupNodeId = "NODE-03"
            ),
            ProductionNode(
                id = "NODE-02",
                name = "Line 2: Heavy Denim Seam Welder",
                banglaName = "লাইন ২: হেভি ডেনিম সীম ওয়েল্ডার",
                lineType = "Garment Metrology",
                status = "DEGRADED",
                efficiencyPercent = 71,
                dailyTargetUnits = 850,
                completedUnits = 460,
                carbonIndexKg = 68.2f,
                backupNodeId = "NODE-04"
            ),
            ProductionNode(
                id = "NODE-03",
                name = "Line 3: Precision Collar Hemming",
                banglaName = "লাইন ৩: প্রিসিশন কলার হেমিং",
                lineType = "Finishing",
                status = "OPTIMAL",
                efficiencyPercent = 98,
                dailyTargetUnits = 1500,
                completedUnits = 1420,
                carbonIndexKg = 31.0f,
                backupNodeId = null
            ),
            ProductionNode(
                id = "NODE-04",
                name = "Line 4: Automated Buttonholer CNC",
                banglaName = "লাইন ৪: অটোমেটেড বাটনহোল সিএনসি",
                lineType = "Hardware",
                status = "BOTTLENECK_REROUTED",
                efficiencyPercent = 88,
                dailyTargetUnits = 2000,
                completedUnits = 1750,
                carbonIndexKg = 54.1f,
                backupNodeId = "NODE-01"
            )
        )
        nodeDao.insertNodes(defaultNodes)

        // Seed initial inspection
        val initialInspection = InspectionRecord(
            id = "INSP-${System.currentTimeMillis()}",
            targetType = "Denim Inseam Stitch",
            hasDefect = true,
            defectLabel = "Skipped Stitch & Tension Loop Deviation",
            banglaDefectLabel = "বাদ পড়া সেলাই এবং টেনশন লুপ বিচ্যুতি",
            confidenceScore = 0.942f,
            boxLeft = 0.22f,
            boxTop = 0.35f,
            boxRight = 0.68f,
            boxBottom = 0.72f,
            severity = "HIGH",
            correctiveAction = "Reroute batch to Line 3 backup hemming station. Trigger automated tension calibration.",
            banglaCorrectiveAction = "ব্যাচটি লাইন ৩ ব্যাকআপ হেমিং স্টেশনে রি-রুট করুন। স্বয়ংক্রিয় টেনশন ক্যালিব্রেশন ট্রিগার করুন।",
            affectedStation = "Line 2"
        )
        inspectionDao.insertInspection(initialInspection)

        // Seed initial voice ERP log
        val initialVoice = VoiceAction(
            id = "VOICE-001",
            transcript = "লাইন ২ এর জন্য ২০ রোল সুতি কাপড় স্টক আপডেট করো",
            language = "bn",
            intent = "RESTOCK_SUPPLIER",
            structuredJson = """{"tool":"RestockSupplier","sku":"FAB-COTTON-20","quantity":20,"unit":"rolls","supplier":"Beximco Textiles","status":"AUTO_APPROVED"}""",
            status = "EXECUTED"
        )
        voiceDao.insertAction(initialVoice)

        // Seed initial payment agreement
        val initialAgreement = PaymentAgreement(
            agreementId = "AGR-BKASH-8801723-9941",
            customerMsisdn = "01723-994120",
            payerReference = "Apex Garments Ltd (Dhaka EPZ)",
            mode = "0011",
            status = "ACTIVE",
            recurringCycle = "THIRTY_DAYS",
            amountBdt = 25000.0
        )
        paymentDao.insertAgreement(initialAgreement)
    }

    suspend fun saveInspection(record: InspectionRecord) {
        inspectionDao.insertInspection(record)
        // If defect is high or critical, update affected station status
        if (record.hasDefect) {
            _telemetry.value = _telemetry.value.copy(
                defectReductionPercent = (_telemetry.value.defectReductionPercent + 0.1).coerceAtMost(48.0)
            )
        }
    }

    suspend fun saveVoiceAction(action: VoiceAction) {
        voiceDao.insertAction(action)
    }

    suspend fun saveAgreement(agreement: PaymentAgreement) {
        paymentDao.insertAgreement(agreement)
        _telemetry.value = _telemetry.value.copy(
            gmvTotalBdt = _telemetry.value.gmvTotalBdt + agreement.amountBdt
        )
    }

    suspend fun removeAgreement(agreementId: String) {
        paymentDao.deleteAgreement(agreementId)
    }

    suspend fun rerouteNode(nodeId: String, newStatus: String) {
        nodeDao.updateStatus(nodeId, newStatus)
    }
}
