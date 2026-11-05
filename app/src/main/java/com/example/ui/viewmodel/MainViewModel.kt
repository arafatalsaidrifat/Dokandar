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
import com.example.payments.DualMarketPaymentEngine
import com.example.web.WebCompanionServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage { EN, BN }
enum class RoleMode { FACTORY_RMG, SME_DOKAN }
enum class NavigationTab { INSPECTION, VOICE_ERP, PAYMENTS, TELEMETRY, WEB_PORTAL }
enum class PaymentRail { BKASH_BDT, STRIPE_USD }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuralisRepository(application)
    private val paymentEngine = DualMarketPaymentEngine()
    val webServer = WebCompanionServer(application, 8080)

    val nodes: StateFlow<List<ProductionNode>> = repository.nodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inspections: StateFlow<List<InspectionRecord>> = repository.inspections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceActions: StateFlow<List<VoiceAction>> = repository.voiceActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agreements: StateFlow<List<PaymentAgreement>> = repository.agreements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val telemetry: StateFlow<TelemetryKpi> = repository.telemetry
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TelemetryKpi())

    // UI Controls State
    private val _language = MutableStateFlow(AppLanguage.EN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _roleMode = MutableStateFlow(RoleMode.FACTORY_RMG)
    val roleMode: StateFlow<RoleMode> = _roleMode.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.INSPECTION)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

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

    private val _bindingStep = MutableStateFlow<DualMarketPaymentEngine.BkashBindingStep>(DualMarketPaymentEngine.BkashBindingStep.Idle)
    val bindingStep: StateFlow<DualMarketPaymentEngine.BkashBindingStep> = _bindingStep.asStateFlow()

    private val _paymentFeedbackMessage = MutableStateFlow<String?>(null)
    val paymentFeedbackMessage: StateFlow<String?> = _paymentFeedbackMessage.asStateFlow()

    init {
        webServer.start()
    }

    override fun onCleared() {
        super.onCleared()
        webServer.stop()
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.EN) AppLanguage.BN else AppLanguage.EN
    }

    fun setRoleMode(mode: RoleMode) {
        _roleMode.value = mode
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    // Module A: Trigger Multimodal Vision Inspection
    fun triggerInspection(targetName: String = "Garment Line 2 Inspection Grid") {
        viewModelScope.launch {
            _isInspecting.value = true
            val record = GeminiAgentEngine.analyzeImageMetrology(targetName, scanPresetIndex++)
            _activeInspection.value = record
            repository.saveInspection(record)
            _isInspecting.value = false
        }
    }

    // Module B: Execute Voice ERP Action
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
            repository.saveVoiceAction(result)
        }
    }

    // Module C: Dual Payment Rails
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
            repository.saveAgreement(agreement)
            _bindingStep.value = DualMarketPaymentEngine.BkashBindingStep.AgreementBound(agreement)
            _paymentFeedbackMessage.value = "bKash Agreement ${agreement.agreementId} bound! Auto-pay activated."
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
            repository.rerouteNode(nodeId, "BOTTLENECK_REROUTED")
            repository.rerouteNode(targetNodeId, "OPTIMAL")
        }
    }
}
