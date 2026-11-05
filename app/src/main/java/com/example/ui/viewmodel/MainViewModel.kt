package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAgentEngine
import com.example.data.model.InspectionRecord
import com.example.data.model.PaymentAgreement
import com.example.data.model.ProductionNode
import com.example.data.model.TelemetryKpi
import com.example.data.model.VoiceAction
import com.example.data.repository.AuralisRepository
import com.example.data.repository.FirestoreAuralisRepository
import com.example.payments.DualMarketPaymentEngine
import com.example.web.WebCompanionServer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage { EN, BN }
enum class RoleMode { FACTORY_RMG, SME_DOKAN }
enum class NavigationTab { INSPECTION, VOICE_ERP, PAYMENTS, TELEMETRY, WEB_PORTAL }
enum class PaymentRail { BKASH_BDT, STRIPE_USD }

data class FieldSwapPreset(
    val id: String,
    val label: String,
    val groundHex: Long,
    val inkHex: Long,
    val cell1Title: String,
    val cell1Value: String,
    val cell1Unit: String,
    val cell2Title: String,
    val cell2Value: String,
    val cell2Unit: String,
    val cell3Title: String,
    val cell3Value: String,
    val cell3Unit: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository = AuralisRepository(application)
    private val firestoreRepository = FirestoreAuralisRepository(application)
    private val paymentEngine = DualMarketPaymentEngine()
    val webServer = WebCompanionServer(application, 8080, localRepository, firestoreRepository)

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _cloudInspections = MutableStateFlow<List<InspectionRecord>>(emptyList())
    private val _cloudVoiceActions = MutableStateFlow<List<VoiceAction>>(emptyList())
    private val _cloudAgreements = MutableStateFlow<List<PaymentAgreement>>(emptyList())
    private val _cloudSyncStatus = MutableStateFlow("FIREBASE CLOUD READY")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    private var firestoreListenersJob: Job? = null

    val nodes: StateFlow<List<ProductionNode>> = localRepository.nodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inspections: StateFlow<List<InspectionRecord>> = combine(
        _cloudInspections,
        localRepository.inspections
    ) { cloud, local ->
        if (cloud.isNotEmpty()) cloud else local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceActions: StateFlow<List<VoiceAction>> = combine(
        _cloudVoiceActions,
        localRepository.voiceActions
    ) { cloud, local ->
        if (cloud.isNotEmpty()) cloud else local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agreements: StateFlow<List<PaymentAgreement>> = combine(
        _cloudAgreements,
        localRepository.agreements
    ) { cloud, local ->
        if (cloud.isNotEmpty()) cloud else local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val telemetry: StateFlow<TelemetryKpi> = localRepository.telemetry
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TelemetryKpi())

    // UI Controls State
    private val _language = MutableStateFlow(AppLanguage.EN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _roleMode = MutableStateFlow(RoleMode.FACTORY_RMG)
    val roleMode: StateFlow<RoleMode> = _roleMode.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.WEB_PORTAL)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Active Operational Console Drawer (defaults to WEB_PORTAL so the Web View is visible immediately)
    private val _activeDrawer = MutableStateFlow<NavigationTab?>(NavigationTab.WEB_PORTAL)
    val activeDrawer: StateFlow<NavigationTab?> = _activeDrawer.asStateFlow()

    // Runtime Field-Swap Presets (Section 6)
    val fieldSwapPresets = listOf(
        FieldSwapPreset(
            id = "RMG_TEXTILE",
            label = "01 / RMG TEXTILE FLOOR",
            groundHex = 0xFFEFEFEE,
            inkHex = 0xFF0D0D0F,
            cell1Title = "OPTICAL SEAM PITCH",
            cell1Value = "0.018",
            cell1Unit = "MM RMS",
            cell2Title = "LINE CADENCE",
            cell2Value = "2,400",
            cell2Unit = "SPM SYNC",
            cell3Title = "DEFECT INTERCEPT",
            cell3Value = "99.4",
            cell3Unit = "% YIELD"
        ),
        FieldSwapPreset(
            id = "CNC_MILLING",
            label = "02 / CNC MICRO-MILLING",
            groundHex = 0xFF0D0D0F,
            inkHex = 0xFFEFEFEE,
            cell1Title = "RUNOUT TOLERANCE",
            cell1Value = "0.004",
            cell1Unit = "MM TIR",
            cell2Title = "SPINDLE TELEMETRY",
            cell2Value = "18.5",
            cell2Unit = "KRPM LOCK",
            cell3Title = "THERMAL DRIFT",
            cell3Value = "0.12",
            cell3Unit = "MICRON/HR"
        ),
        FieldSwapPreset(
            id = "DOKAN_SME",
            label = "03 / DOKAN SME LEDGER",
            groundHex = 0xFFE4E4E2,
            inkHex = 0xFF0D0D0F,
            cell1Title = "VOICE ASR LATENCY",
            cell1Value = "140",
            cell1Unit = "MS BANGLA",
            cell2Title = "BKASH MODE 0011",
            cell2Value = "3550",
            cell2Unit = "SEC TOKEN",
            cell3Title = "AUTO-PAY SETTLEMENT",
            cell3Value = "100",
            cell3Unit = "% VERIFIED"
        )
    )

    private val _selectedFieldPreset = MutableStateFlow(fieldSwapPresets.first())
    val selectedFieldPreset: StateFlow<FieldSwapPreset> = _selectedFieldPreset.asStateFlow()

    // Inspection state
    private val _isInspecting = MutableStateFlow(false)
    val isInspecting: StateFlow<Boolean> = _isInspecting.asStateFlow()

    private val _activeInspection = MutableStateFlow<InspectionRecord?>(null)
    val activeInspection: StateFlow<InspectionRecord?> = _activeInspection.asStateFlow()

    private var scanPresetIndex = 0

    // Voice ERP state
    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    private val _voiceInputText = MutableStateFlow("")
    val voiceInputText: StateFlow<String> = _voiceInputText.asStateFlow()

    private val _latestVoiceResult = MutableStateFlow<VoiceAction?>(null)
    val latestVoiceResult: StateFlow<VoiceAction?> = _latestVoiceResult.asStateFlow()

    // Payment state
    private val _activePaymentRail = MutableStateFlow(PaymentRail.BKASH_BDT)
    val activePaymentRail: StateFlow<PaymentRail> = _activePaymentRail.asStateFlow()

    private val _bindingStep = MutableStateFlow<DualMarketPaymentEngine.BkashBindingStep>(
        DualMarketPaymentEngine.BkashBindingStep.Idle
    )
    val bindingStep: StateFlow<DualMarketPaymentEngine.BkashBindingStep> = _bindingStep.asStateFlow()

    private val _paymentFeedbackMessage = MutableStateFlow<String?>(null)
    val paymentFeedbackMessage: StateFlow<String?> = _paymentFeedbackMessage.asStateFlow()

    init {
        webServer.start()
    }

    override fun onCleared() {
        super.onCleared()
        firestoreListenersJob?.cancel()
        webServer.stop()
    }

    fun bindAuthenticatedUser(uid: String?) {
        if (_currentUserId.value == uid && firestoreListenersJob?.isActive == true) return
        _currentUserId.value = uid
        firestoreListenersJob?.cancel()

        if (uid == null) {
            _cloudInspections.value = emptyList()
            _cloudVoiceActions.value = emptyList()
            _cloudAgreements.value = emptyList()
            _cloudSyncStatus.value = "AUTH REQUIRED"
            return
        }

        if (uid == "dev-operator" && com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null) {
            _cloudSyncStatus.value = "DEV-OPERATOR // FLOOR ACTIVE"
            return
        }

        _cloudSyncStatus.value = "FIRESTORE LIVE // ${uid.take(8).uppercase()}"
        firestoreListenersJob = viewModelScope.launch {
            launch {
                firestoreRepository.observeInspections()
                    .catch { _cloudSyncStatus.value = "LOCAL CACHE ACTIVE" }
                    .collect { list ->
                        _cloudInspections.value = list
                        if (list.isEmpty()) {
                            seedInitialCloudData(uid)
                        }
                    }
            }
            launch {
                firestoreRepository.observeVoiceActions()
                    .catch { }
                    .collect { list ->
                        _cloudVoiceActions.value = list
                    }
            }
            launch {
                firestoreRepository.observePaymentAgreements()
                    .catch { }
                    .collect { list ->
                        _cloudAgreements.value = list
                    }
            }
        }
    }

    private suspend fun seedInitialCloudData(uid: String) {
        try {
            val existing = firestoreRepository.getUserInspections().getOrNull().orEmpty()
            if (existing.isNotEmpty()) return

            firestoreRepository.createInspection(
                InspectionRecord(
                    userId = uid,
                    targetType = "Denim Inseam Optical Array",
                    hasDefect = true,
                    defectLabel = "Skipped Stitch & Tension Loop Deviation",
                    banglaDefectLabel = "বাদ পড়া সেলাই এবং টেনশন লুপ বিচ্যুতি",
                    confidenceScore = 0.964f,
                    boxLeft = 0.22f,
                    boxTop = 0.35f,
                    boxRight = 0.68f,
                    boxBottom = 0.72f,
                    severity = "HIGH",
                    correctiveAction = "Reroute batch to Line 3 backup hemming station. Trigger automated tension calibration.",
                    banglaCorrectiveAction = "ব্যাচটি লাইন ৩ ব্যাকআপ হেমিং স্টেশনে রি-রুট করুন। স্বয়ংক্রিয় টেনশন ক্যালিব্রেশন ট্রিগার করুন।",
                    affectedStation = "Line 2"
                )
            )
            firestoreRepository.createVoiceAction(
                VoiceAction(
                    userId = uid,
                    transcript = "লাইন ২ এর জন্য ২০ রোল সুতি কাপড় স্টক আপডেট করো",
                    language = "bn",
                    intent = "RESTOCK_SUPPLIER",
                    structuredJson = """{"tool":"RestockSupplier","sku":"FAB-COTTON-20","quantity":20,"unit":"rolls","supplier":"Beximco Textiles","status":"AUTO_APPROVED"}""",
                    status = "EXECUTED"
                )
            )
            firestoreRepository.createPaymentAgreement(
                PaymentAgreement(
                    userId = uid,
                    agreementCode = "AGR-BKASH-8801723-9941",
                    customerMsisdn = "01723-994120",
                    payerReference = "Apex Garments Ltd (Dhaka EPZ)",
                    mode = "0011",
                    status = "ACTIVE",
                    recurringCycle = "THIRTY_DAYS",
                    amountBdt = 25000.0
                )
            )
        } catch (ignored: Exception) {
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.EN) AppLanguage.BN else AppLanguage.EN
    }

    fun setRoleMode(mode: RoleMode) {
        _roleMode.value = mode
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
        _activeDrawer.value = tab
    }

    fun openOperationalDrawer(tab: NavigationTab) {
        _currentTab.value = tab
        _activeDrawer.value = tab
    }

    fun closeOperationalDrawer() {
        _activeDrawer.value = null
    }

    fun selectFieldSwapPreset(preset: FieldSwapPreset) {
        _selectedFieldPreset.value = preset
    }

    // Module A: Trigger Multimodal Vision Inspection (Firebase AI Logic + Firestore)
    fun triggerInspection(targetName: String = "Garment Line 2 Inspection Grid") {
        viewModelScope.launch {
            _isInspecting.value = true
            val record = GeminiAgentEngine.analyzeImageMetrology(targetName, scanPresetIndex++)
            _activeInspection.value = record
            localRepository.saveInspection(record)
            _currentUserId.value?.let { uid ->
                firestoreRepository.createInspection(record.copy(userId = uid))
                    .onSuccess { _cloudSyncStatus.value = "FIRESTORE SYNCED // INSP" }
                    .onFailure { _cloudSyncStatus.value = "LOCAL MIRROR SAVED" }
            }
            _isInspecting.value = false
        }
    }

    // Module B: Execute Voice ERP Action (Firebase AI Logic + Firestore)
    fun updateVoiceInput(text: String) {
        _voiceInputText.value = text
    }

    fun toggleVoiceListening() {
        _isVoiceListening.value = !_isVoiceListening.value
        if (_isVoiceListening.value) {
            val samplePrompt = if (_language.value == AppLanguage.BN) {
                if (_roleMode.value == RoleMode.FACTORY_RMG) {
                    "লাইন ২ ব্রেকডাউন - রুট লাইন ৩ এ ট্রান্সফার করো"
                } else {
                    "মিরপুর দোকানের জন্য ১০০ পিস টি-শার্ট ইনভয়েস তৈরি করো"
                }
            } else {
                if (_roleMode.value == RoleMode.FACTORY_RMG) {
                    "Restock 250 bales carded cotton yarn from Square Textiles"
                } else {
                    "Create invoice for 500 polo shirts with bKash Auto-Pay"
                }
            }
            _voiceInputText.value = samplePrompt
        }
    }

    fun submitVoiceCommand() {
        val prompt = _voiceInputText.value.ifBlank {
            if (_language.value == AppLanguage.BN) "লাইন ৩ এর জন্য ৫০ গজ সুতি কাপড় রিস্টক করো" else "Create invoice for 500 polo shirts"
        }
        viewModelScope.launch {
            _isVoiceListening.value = false
            val result = GeminiAgentEngine.executeVoiceCommand(prompt, _language.value == AppLanguage.BN)
            _latestVoiceResult.value = result
            localRepository.saveVoiceAction(result)
            _currentUserId.value?.let { uid ->
                firestoreRepository.createVoiceAction(result.copy(userId = uid))
                    .onSuccess { _cloudSyncStatus.value = "FIRESTORE SYNCED // ERP" }
                    .onFailure { _cloudSyncStatus.value = "LOCAL MIRROR SAVED" }
            }
        }
    }

    // Module C: Dual Payment Rails (bKash Mode 0011 + Stripe USD + Firestore)
    fun setPaymentRail(rail: PaymentRail) {
        _activePaymentRail.value = rail
    }

    fun startBkashBinding() {
        _bindingStep.value = DualMarketPaymentEngine.BkashBindingStep.WalletInput("01723-994120")
    }

    fun submitBkashWallet(msisdn: String, customerName: String, amountBdt: Double) {
        viewModelScope.launch {
            val step = paymentEngine.initiateAgreement(msisdn, customerName, amountBdt)
            _bindingStep.value = step
        }
    }

    fun submitBkashOtp(msisdn: String, otp: String) {
        viewModelScope.launch {
            val step = paymentEngine.verifyOtpAndPromptPin(msisdn, otp)
            _bindingStep.value = step
        }
    }

    fun submitBkashPinAndBind(msisdn: String, pin: String, customerName: String, amountBdt: Double) {
        viewModelScope.launch {
            val agreement = paymentEngine.executeAgreement(msisdn, pin, customerName, amountBdt)
            localRepository.saveAgreement(agreement)
            _currentUserId.value?.let { uid ->
                firestoreRepository.createPaymentAgreement(
                    agreement.copy(
                        userId = uid,
                        agreementCode = agreement.agreementId
                    )
                )
                    .onSuccess { _cloudSyncStatus.value = "FIRESTORE SYNCED // 0011" }
                    .onFailure { _cloudSyncStatus.value = "LOCAL MIRROR SAVED" }
            }
            _bindingStep.value = DualMarketPaymentEngine.BkashBindingStep.AgreementBound(agreement)
            _paymentFeedbackMessage.value = "bKash Agreement ${agreement.agreementId} bound & synced to Firestore!"
        }
    }

    fun cancelBkashBinding() {
        _bindingStep.value = DualMarketPaymentEngine.BkashBindingStep.Idle
    }

    fun triggerScheduledAutoPay(agreementId: String, amountBdt: Double) {
        viewModelScope.launch {
            val success = paymentEngine.executeRecurringAutoPay(agreementId, amountBdt)
            if (success) {
                _paymentFeedbackMessage.value = "Scheduled 30-Day Auto-Pay ৳${amountBdt.toInt()} executed via Agreement $agreementId"
            }
        }
    }

    fun dismissPaymentMessage() {
        _paymentFeedbackMessage.value = null
    }

    fun rerouteProductionStation(nodeId: String, targetNodeId: String) {
        viewModelScope.launch {
            localRepository.rerouteNode(nodeId, "BOTTLENECK_REROUTED")
            localRepository.rerouteNode(targetNodeId, "OPTIMAL")
        }
    }
}
