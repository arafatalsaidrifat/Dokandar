package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BilingualTopBar
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.DualPaymentScreen
import com.example.ui.screens.GrantTelemetryScreen
import com.example.ui.screens.VisionInspectionScreen
import com.example.ui.screens.VoiceErpScreen
import com.example.ui.screens.WebCompanionScreen
import com.example.ui.theme.AuralisOSTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuralisOSTheme(darkTheme = true) {
                AuralisMainApp()
            }
        }
    }
}

@Composable
fun AuralisMainApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current

    // Request permissions for Camera and Audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    // State collections
    val language by viewModel.language.collectAsStateWithLifecycle()
    val roleMode by viewModel.roleMode.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val nodes by viewModel.nodes.collectAsStateWithLifecycle()
    val isInspecting by viewModel.isInspecting.collectAsStateWithLifecycle()
    val activeInspection by viewModel.activeInspection.collectAsStateWithLifecycle()

    val isVoiceListening by viewModel.isVoiceListening.collectAsStateWithLifecycle()
    val voiceInputText by viewModel.voiceInputText.collectAsStateWithLifecycle()
    val latestVoiceResult by viewModel.latestVoiceResult.collectAsStateWithLifecycle()
    val voiceActions by viewModel.voiceActions.collectAsStateWithLifecycle()

    val activePaymentRail by viewModel.activePaymentRail.collectAsStateWithLifecycle()
    val bindingStep by viewModel.bindingStep.collectAsStateWithLifecycle()
    val agreements by viewModel.agreements.collectAsStateWithLifecycle()
    val paymentFeedback by viewModel.paymentFeedbackMessage.collectAsStateWithLifecycle()

    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

    // Back handling: Return to Inspection Home screen if inside sub-screens
    BackHandler(enabled = currentTab != NavigationTab.INSPECTION) {
        viewModel.selectTab(NavigationTab.INSPECTION)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color(0xFF0B0F19),
        topBar = {
            BilingualTopBar(
                language = language,
                roleMode = roleMode,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSelectRole = { viewModel.setRoleMode(it) }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = currentTab,
                language = language,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0B0F19))
        ) {
            when (currentTab) {
                NavigationTab.INSPECTION -> {
                    VisionInspectionScreen(
                        language = language,
                        isInspecting = isInspecting,
                        activeInspection = activeInspection,
                        nodes = nodes,
                        telemetry = telemetry,
                        onTriggerScan = { viewModel.triggerInspection() },
                        onRerouteNode = { source, target -> viewModel.rerouteProductionStation(source, target) }
                    )
                }

                NavigationTab.VOICE_ERP -> {
                    VoiceErpScreen(
                        language = language,
                        isListening = isVoiceListening,
                        inputText = voiceInputText,
                        latestResult = latestVoiceResult,
                        allActions = voiceActions,
                        onInputChange = { viewModel.updateVoiceInput(it) },
                        onToggleListening = { viewModel.toggleVoiceListening() },
                        onSubmitCommand = { viewModel.submitVoiceCommand() }
                    )
                }

                NavigationTab.PAYMENTS -> {
                    DualPaymentScreen(
                        language = language,
                        activeRail = activePaymentRail,
                        bindingStep = bindingStep,
                        agreements = agreements,
                        feedbackMessage = paymentFeedback,
                        onSelectRail = { viewModel.setPaymentRail(it) },
                        onStartBinding = { viewModel.startBkashBinding() },
                        onSubmitWallet = { msisdn, name, amount -> viewModel.submitBkashWallet(msisdn, name, amount) },
                        onSubmitOtp = { msisdn, otp -> viewModel.submitBkashOtp(msisdn, otp) },
                        onSubmitPin = { msisdn, pin, name, amount -> viewModel.submitBkashPinAndBind(msisdn, pin, name, amount) },
                        onCancelBinding = { viewModel.cancelBkashBinding() },
                        onTriggerAutoPay = { agrId, amount -> viewModel.triggerScheduledAutoPay(agrId, amount) },
                        onDismissFeedback = { viewModel.dismissPaymentMessage() }
                    )
                }

                NavigationTab.TELEMETRY -> {
                    GrantTelemetryScreen(
                        language = language,
                        telemetry = telemetry
                    )
                }

                NavigationTab.WEB_PORTAL -> {
                    WebCompanionScreen(
                        language = language,
                        portalUrl = viewModel.webServer.getPortalUrl()
                    )
                }
            }
        }
    }
}
