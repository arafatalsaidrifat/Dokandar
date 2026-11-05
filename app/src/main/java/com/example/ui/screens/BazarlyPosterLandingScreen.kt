package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.components.AuralisStudioSubject
import com.example.ui.theme.AchromaticGround
import com.example.ui.theme.ArchivoDisplayFamily
import com.example.ui.theme.DimmerStageGround
import com.example.ui.theme.HairlineRule
import com.example.ui.theme.IbmPlexMonoFamily
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.InvertedHairlineRule
import com.example.ui.theme.SignalAccent
import com.example.ui.theme.SignalAccentLift
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun BazarlyPosterLandingScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit
) {
    val activeDrawer by viewModel.activeDrawer.collectAsState()
    val language by viewModel.language.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val selectedFieldPreset by viewModel.selectedFieldPreset.collectAsState()
    val inspections by viewModel.inspections.collectAsState()
    val voiceActions by viewModel.voiceActions.collectAsState()
    val agreements by viewModel.agreements.collectAsState()
    val nodes by viewModel.nodes.collectAsState()
    val isInspecting by viewModel.isInspecting.collectAsState()
    val activeInspection by viewModel.activeInspection.collectAsState()
    val isVoiceListening by viewModel.isVoiceListening.collectAsState()
    val voiceInputText by viewModel.voiceInputText.collectAsState()
    val latestVoiceResult by viewModel.latestVoiceResult.collectAsState()
    val activePaymentRail by viewModel.activePaymentRail.collectAsState()
    val bindingStep by viewModel.bindingStep.collectAsState()
    val paymentFeedback by viewModel.paymentFeedbackMessage.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()

    val scrollState = rememberScrollState()

    // Scroll-driven mechanics for Section 4 (Counter-Travel Stage)
    val turretRotation by remember {
        derivedStateOf {
            (scrollState.value * 0.08f) % 360f
        }
    }
    val marqueeOffsetPx by remember {
        derivedStateOf {
            -((scrollState.value * 0.35f).roundToInt() % 900)
        }
    }

    // Handle system back when an operational console drawer is open
    if (activeDrawer != null) {
        BackHandler {
            viewModel.closeOperationalDrawer()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AchromaticGround)
    ) {
        // 1. FIXED 64DP TECHNICAL NAVIGATION BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(AchromaticGround.copy(alpha = 0.90f))
                .zIndex(20f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand: BAZARLY in Archivo 800 at 16px, tracking -0.035em + period in SignalAccent
                Text(
                    text = buildAnnotatedString {
                        append("BAZARLY")
                        withStyle(SpanStyle(color = SignalAccent)) {
                            append(".")
                        }
                    },
                    fontFamily = ArchivoDisplayFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = (-0.035).em,
                    color = InkPrimary,
                    modifier = Modifier
                        .clickable { viewModel.closeOperationalDrawer() }
                        .padding(vertical = 8.dp)
                        .testTag("nav_brand_home")
                )

                // Five mono links at 11px, tracking 0.14em, uppercase + 1px bottom accent border
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navItems = listOf(
                        NavigationTab.INSPECTION to "01.VISION",
                        NavigationTab.VOICE_ERP to "02.VOICE",
                        NavigationTab.PAYMENTS to "03.PAY",
                        NavigationTab.TELEMETRY to "04.PROOF",
                        NavigationTab.WEB_PORTAL to "05.WEB"
                    )
                    navItems.forEach { (tab, label) ->
                        val isSelected = activeDrawer == tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    if (isSelected) viewModel.closeOperationalDrawer()
                                    else viewModel.openOperationalDrawer(tab)
                                }
                                .padding(vertical = 6.dp)
                                .testTag("nav_link_${tab.name.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                fontFamily = IbmPlexMonoFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                letterSpacing = 0.14.em,
                                color = if (isSelected) SignalAccent else InkPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(42.dp)
                                    .height(1.dp)
                                    .background(if (isSelected) SignalAccent else Color.Transparent)
                            )
                        }
                    }
                }

                // Pill buttons: Language + Sign Out
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleLanguage() },
                        shape = RoundedCornerShape(999.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("language_toggle_pill")
                    ) {
                        Text(
                            text = if (language == AppLanguage.EN) "BN" else "EN",
                            fontFamily = IbmPlexMonoFamily,
                            fontSize = 10.sp,
                            letterSpacing = 0.14.em,
                            color = InkPrimary
                        )
                    }

                    Button(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InkPrimary,
                            contentColor = AchromaticGround
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("sign_out_pill_button")
                    ) {
                        Text(
                            text = "EXIT",
                            fontFamily = IbmPlexMonoFamily,
                            fontSize = 10.sp,
                            letterSpacing = 0.14.em
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = HairlineRule)
        }

        // If an operational console drawer is active, show it with a top technical return bar
        if (activeDrawer != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp)
                    .background(AchromaticGround)
            ) {
                // Sub-console technical breadcrumb bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DimmerStageGround)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clickable { viewModel.closeOperationalDrawer() }
                            .testTag("back_to_poster_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SignalAccent, CircleShape)
                        )
                        Text(
                            text = "← RETURN TO BAZARLY POSTER STAGE",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkPrimary
                        )
                    }
                    Text(
                        text = cloudSyncStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                Box(modifier = Modifier.weight(1f)) {
                    when (activeDrawer) {
                        NavigationTab.INSPECTION -> VisionInspectionScreen(
                            language = language,
                            isInspecting = isInspecting,
                            activeInspection = activeInspection,
                            nodes = nodes,
                            telemetry = telemetry,
                            onTriggerScan = { viewModel.triggerInspection() },
                            onRerouteNode = { src, dst -> viewModel.rerouteProductionStation(src, dst) }
                        )
                        NavigationTab.VOICE_ERP -> VoiceErpScreen(
                            language = language,
                            isListening = isVoiceListening,
                            inputText = voiceInputText,
                            latestResult = latestVoiceResult,
                            allActions = voiceActions,
                            onInputChange = { viewModel.updateVoiceInput(it) },
                            onToggleListening = { viewModel.toggleVoiceListening() },
                            onSubmitCommand = { viewModel.submitVoiceCommand() }
                        )
                        NavigationTab.PAYMENTS -> DualPaymentScreen(
                            language = language,
                            activeRail = activePaymentRail,
                            bindingStep = bindingStep,
                            agreements = agreements,
                            feedbackMessage = paymentFeedback,
                            onSelectRail = { viewModel.setPaymentRail(it) },
                            onStartBinding = { viewModel.startBkashBinding() },
                            onSubmitWallet = { msisdn, name, amt -> viewModel.submitBkashWallet(msisdn, name, amt) },
                            onSubmitOtp = { msisdn, otp -> viewModel.submitBkashOtp(msisdn, otp) },
                            onSubmitPin = { msisdn, pin, name, amt -> viewModel.submitBkashPinAndBind(msisdn, pin, name, amt) },
                            onCancelBinding = { viewModel.cancelBkashBinding() },
                            onTriggerAutoPay = { agrId, amt -> viewModel.triggerScheduledAutoPay(agrId, amt) },
                            onDismissFeedback = { viewModel.dismissPaymentMessage() }
                        )
                        NavigationTab.TELEMETRY -> GrantTelemetryScreen(
                            language = language,
                            telemetry = telemetry
                        )
                        NavigationTab.WEB_PORTAL -> WebCompanionScreen(
                            language = language,
                            portalUrl = viewModel.webServer.getPortalUrl(),
                            apkSizeMb = viewModel.webServer.getApkSizeFormatted(),
                            apkSha256 = viewModel.webServer.getApkSha256Short()
                        )
                        null -> {}
                    }
                }
            }
        } else {
            // 7-SECTION POSTER-SCALE ACHROMATIC TECHNICAL-PRODUCT SCROLL EXPERIENCE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp)
                    .verticalScroll(scrollState)
                    .testTag("bazarly_poster_scroll")
            ) {
                // =========================================================================
                // SECTION 2: HERO ON DIMMER STAGE GROUND (#E4E4E2) WITH SWISS-INDUSTRIAL STACK
                // =========================================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DimmerStageGround)
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 540.dp)
                    ) {
                        // Single-line telemetry metadata with proper // delimiter and ellipsis
                        Text(
                            text = "SYS.01 // BZR-OS-4.2 // ASIA-SE1 // 23.8103° N, 90.4125° E",
                            fontFamily = IbmPlexMonoFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            letterSpacing = 0.06.em,
                            color = InkMuted,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "OPTICAL INSPECTION // BILINGUAL ERP // 0011 RAILS",
                            fontFamily = IbmPlexMonoFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.5.sp,
                            letterSpacing = 0.10.em,
                            color = InkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Display headline with final phrase in #2F5BFF
                        Text(
                            text = buildAnnotatedString {
                                append("Sub-millimeter garment telemetry and autonomous ")
                                withStyle(SpanStyle(color = SignalAccent)) {
                                    append("supply execution.")
                                }
                            },
                            fontFamily = ArchivoDisplayFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            lineHeight = 34.sp,
                            letterSpacing = (-0.045).em,
                            color = InkPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Lede
                        Text(
                            text = "Closed-loop optical defect interception paired with Bangla/English voice ledger dispatch and tokenized bKash Mode 0011 settlement.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = InkSecondary,
                            modifier = Modifier.widthIn(max = 380.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Pill Buttons Row (including GET APP / WEB)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.openOperationalDrawer(NavigationTab.WEB_PORTAL) },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = InkPrimary,
                                    contentColor = AchromaticGround
                                ),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("hero_get_app_web_button")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SignalAccent, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GET APP / WEB ↓",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.10.em
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.openOperationalDrawer(NavigationTab.INSPECTION) },
                                shape = RoundedCornerShape(999.dp),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("hero_launch_vision_button")
                            ) {
                                Text(
                                    text = "LAUNCH VISION",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.10.em,
                                    color = InkPrimary
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.openOperationalDrawer(NavigationTab.VOICE_ERP) },
                                shape = RoundedCornerShape(999.dp),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("hero_launch_voice_button")
                            ) {
                                Text(
                                    text = "VOICE ERP (BN/EN)",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.10.em,
                                    color = InkPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(thickness = 1.dp, color = HairlineRule)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Two-column Specification Rows (SPEC.01 – SPEC.03) with 6dp gap and no orphan wrapping
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SPEC.01 // OPTICS:",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.04.em,
                                    color = InkMuted,
                                    maxLines = 1
                                )
                                Text(
                                    text = "0.018 MM RMS TELECENTRIC",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.04.em,
                                    color = InkPrimary,
                                    maxLines = 1
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SPEC.02 // CLOUD ENGINE:",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.04.em,
                                    color = InkMuted,
                                    maxLines = 1
                                )
                                Text(
                                    text = "FIRESTORE + GEMINI 3.8 FLASH",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.04.em,
                                    color = InkPrimary,
                                    maxLines = 1
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SPEC.03 // SETTLEMENT RAILS:",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.04.em,
                                    color = InkMuted,
                                    maxLines = 1
                                )
                                Text(
                                    text = "BKASH 0011 + STRIPE USD",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.04.em,
                                    color = InkPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stacked Lower Visual Anchor: Camera Rig in middle-right above full-contrast BAZARLY wordmark
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(end = 180.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "TELECENTRIC RIG // Ø64MM",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.06.em,
                                    color = InkMuted
                                )
                                Text(
                                    text = cloudSyncStatus,
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.06.em,
                                    color = SignalAccent
                                )
                            }

                            AuralisStudioSubject(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(width = 188.dp, height = 148.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(thickness = 1.dp, color = HairlineRule)
                        Spacer(modifier = Modifier.height(6.dp))

                        SafeAreaPosterWordmark(
                            word = "BAZARLY",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                // =========================================================================
                // SECTION 3: INVERTED PROOF SECTION (#0D0D0F FULL-BLEED INK GROUND)
                // =========================================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InkPrimary)
                        .padding(horizontal = 20.dp, vertical = 48.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "02 / CLOSED-LOOP VARIANCE PROOF",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkMuted
                        )
                        Text(
                            text = "SCALE // 1:1 SUMMED SINE HARMONICS",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalAccentLift
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Drift-free stitch tension across 2,400 RPM shifts.",
                        fontFamily = ArchivoDisplayFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.04).em,
                        color = AchromaticGround
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Uncompensated factory lines wander up to ±1.45 mm across thermal cycles. Bazarly's optical feedback loop clamps variance to a flat harmonic trace.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFA8A9B2)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Enormous tabular figure 0.018 + baseline-aligned mono unit mm
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "0.018",
                            fontFamily = ArchivoDisplayFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 68.sp,
                            lineHeight = 56.sp,
                            letterSpacing = (-0.05).em,
                            color = AchromaticGround
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MM RMS DEVIATION",
                            style = MaterialTheme.typography.labelLarge,
                            color = SignalAccentLift,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Summed Sine Waveform Trace Canvas (Reference vs #7C97FF Accent Trace)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .border(1.dp, InvertedHairlineRule)
                            .padding(12.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Horizontal datum hairlines
                            for (i in 1..3) {
                                val y = h * (i / 4f)
                                drawLine(
                                    color = Color(0x22FFFFFF),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1f
                                )
                            }

                            val refPath = Path()
                            val accPath = Path()
                            val steps = 120
                            for (i in 0..steps) {
                                val t = i.toFloat() / steps
                                val x = t * w
                                val domainX = t * 600f

                                // y_ref(x) = 78 + 38*sin(x*0.022) + 21*sin(x*0.061+1.1) + 14*cos(x*0.13)
                                val rawRef = 78f +
                                    38f * sin(domainX * 0.022f) +
                                    21f * sin(domainX * 0.061f + 1.1f) +
                                    14f * cos(domainX * 0.13f)
                                val yRef = (rawRef / 160f).coerceIn(0.05f, 0.95f) * h

                                // y_acc(x) = 88 + 1.8*sin(x*0.05) + 0.9*cos(x*0.14)
                                val rawAcc = 88f +
                                    1.8f * sin(domainX * 0.05f) +
                                    0.9f * cos(domainX * 0.14f)
                                val yAcc = (rawAcc / 160f).coerceIn(0.05f, 0.95f) * h

                                if (i == 0) {
                                    refPath.moveTo(x, yRef)
                                    accPath.moveTo(x, yAcc)
                                } else {
                                    refPath.lineTo(x, yRef)
                                    accPath.lineTo(x, yAcc)
                                }
                            }

                            // Faint wandering reference trace
                            drawPath(
                                path = refPath,
                                color = Color(0x55A8A9B2),
                                style = Stroke(width = 2f, cap = StrokeCap.Round)
                            )

                            // #7C97FF SignalAccentLift flat closed-loop trace
                            drawPath(
                                path = accPath,
                                color = SignalAccentLift,
                                style = Stroke(width = 3f, cap = StrokeCap.Round)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TRACE.A // UNCOMPENSATED LINE (±1.45 MM)",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkMuted
                            )
                            Text(
                                text = "TRACE.B // BAZARLY LOCK (0.018 MM)",
                                style = MaterialTheme.typography.labelSmall,
                                color = SignalAccentLift
                            )
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                // =========================================================================
                // SECTION 4: PINNED COUNTER-TRAVEL STAGE (ROTATING TURRET + MARQUEE)
                // =========================================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .background(DimmerStageGround)
                        .clipToBounds()
                ) {
                    // Top-left mono label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "03 / COUNTER-TRAVEL OPTICAL STAGE",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkMuted
                        )
                        Text(
                            text = "SCROLL-LINKED KINEMATICS",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSecondary
                        )
                    }

                    // Layer 1 (z-index 1): Opposing horizontal marquee at 0.14 opacity
                    Text(
                        text = "ZERO DEFECT — ZERO DEFECT — ZERO DEFECT — ZERO DEFECT",
                        fontFamily = ArchivoDisplayFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 92.sp,
                        lineHeight = 74.sp,
                        letterSpacing = (-0.05).em,
                        color = InkPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset { IntOffset(marqueeOffsetPx, 0) }
                            .alpha(0.14f)
                            .zIndex(1f)
                    )

                    // Layer 2 (z-index 2): Centered rotating optical metrology turret
                    AuralisStudioSubject(
                        rotationDegrees = turretRotation,
                        modifier = Modifier
                            .size(230.dp)
                            .align(Alignment.Center)
                            .zIndex(2f)
                    )

                    // Bottom-right live mono readout driven by scroll state
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(20.dp)
                            .zIndex(3f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AXIS.Z // TELECENTRIC TURRET",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(SignalAccent, CircleShape)
                            )
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "TURRET AZIMUTH: %05.1f° // TRAVEL: %d PX",
                                    turretRotation,
                                    scrollState.value
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = InkPrimary
                            )
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                // =========================================================================
                // SECTION 5: TECHNICAL DIAGRAM + INTERACTIVE OPERATIONS CONSOLE (#EFEFEE)
                // =========================================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AchromaticGround)
                        .padding(horizontal = 20.dp, vertical = 44.dp)
                ) {
                    Text(
                        text = "04 / TO-SCALE SCHEMATIC & LIVE OPERATIONS",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Optical geometry wired directly to Firestore & payment rails.",
                        fontFamily = ArchivoDisplayFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 29.sp,
                        letterSpacing = (-0.04).em,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Inspect real-time seam tolerances, issue Bangla/English voice ERP dispatches, or bind bKash Mode 0011 agreements.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hairline-ruled definition list (mono label in accent, plain description, mono value right-aligned)
                    val schematicDefs = listOf(
                        Triple("POI.01", "Coaxial Telecentric Objective", "F/2.8 // 0.018 MM"),
                        Triple("POI.02", "Laser Line Triangulation Diode", "450 NM // 2.4 KHZ"),
                        Triple("POI.03", "Gemini 3.8 Multimodal Edge Pipe", "JSON SCHEMA LOCK"),
                        Triple("POI.04", "bKash Tokenized Checkout 0011", "3550S SINGLE-FLIGHT")
                    )
                    schematicDefs.forEach { (code, desc, value) ->
                        HorizontalDivider(thickness = 1.dp, color = HairlineRule)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelLarge,
                                color = SignalAccent,
                                modifier = Modifier.width(68.dp)
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.labelSmall,
                                color = InkSecondary
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                    Spacer(modifier = Modifier.height(24.dp))

                    // To-Scale Technical Line Drawing Canvas (1.5px strokes, construction geometry, #2F5BFF POIs)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .border(1.dp, HairlineRule)
                            .background(DimmerStageGround)
                            .padding(14.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val center = Offset(w * 0.5f, h * 0.52f)
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)

                            // Construction centerlines
                            drawLine(
                                color = Color(0x330D0D0F),
                                start = Offset(0f, center.y),
                                end = Offset(w, center.y),
                                strokeWidth = 1f,
                                pathEffect = dashEffect
                            )
                            drawLine(
                                color = Color(0x330D0D0F),
                                start = Offset(center.x, 0f),
                                end = Offset(center.x, h),
                                strokeWidth = 1f,
                                pathEffect = dashEffect
                            )

                            // Concentric construction circles
                            drawCircle(
                                color = Color(0x280D0D0F),
                                radius = h * 0.42f,
                                center = center,
                                style = Stroke(width = 1f, pathEffect = dashEffect)
                            )
                            drawCircle(
                                color = InkPrimary,
                                radius = h * 0.30f,
                                center = center,
                                style = Stroke(width = 2f)
                            )
                            drawCircle(
                                color = InkPrimary,
                                radius = h * 0.16f,
                                center = center,
                                style = Stroke(width = 1.5f)
                            )

                            // Outer schematic chassis profile
                            drawRect(
                                color = InkPrimary,
                                topLeft = Offset(w * 0.18f, h * 0.18f),
                                size = androidx.compose.ui.geometry.Size(w * 0.64f, h * 0.68f),
                                style = Stroke(width = 1.8f)
                            )

                            // Points of Interest (#2F5BFF) + Leader Lines
                            val poi1 = Offset(center.x, center.y - h * 0.30f)
                            val poi1Label = Offset(w * 0.12f, h * 0.12f)
                            drawLine(SignalAccent, poi1, poi1Label, strokeWidth = 1.5f)
                            drawCircle(SignalAccent, radius = 4.5f, center = poi1)

                            val poi2 = Offset(center.x + h * 0.16f, center.y)
                            val poi2Label = Offset(w * 0.86f, h * 0.24f)
                            drawLine(SignalAccent, poi2, poi2Label, strokeWidth = 1.5f)
                            drawCircle(SignalAccent, radius = 4.5f, center = poi2)

                            val poi3 = Offset(w * 0.82f, h * 0.72f)
                            val poi3Label = Offset(w * 0.88f, h * 0.84f)
                            drawLine(SignalAccent, poi3, poi3Label, strokeWidth = 1.5f)
                            drawCircle(SignalAccent, radius = 4.5f, center = poi3)
                        }

                        Text(
                            text = "POI.01 // OBJECTIVE Ø64MM",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalAccent,
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                        Text(
                            text = "POI.02 // LASER EMITTER",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalAccent,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                        Text(
                            text = "POI.03 // CMOS GLOBAL SHUTTER",
                            style = MaterialTheme.typography.labelSmall,
                            color = SignalAccent,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Interactive Operations Console Triggers (Pill Buttons)
                    Text(
                        text = "INTERACTIVE CONSOLES // CLICK TO OPEN LIVE MODULE",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val consoleButtons = listOf(
                            Triple(NavigationTab.INSPECTION, "01 / VISION METROLOGY (${inspections.size})", "open_vision_drawer"),
                            Triple(NavigationTab.VOICE_ERP, "02 / VOICE ERP (${voiceActions.size})", "open_voice_drawer"),
                            Triple(NavigationTab.PAYMENTS, "03 / BKASH 0011 & STRIPE (${agreements.size})", "open_payments_drawer"),
                            Triple(NavigationTab.TELEMETRY, "04 / GRANT TELEMETRY", "open_telemetry_drawer"),
                            Triple(NavigationTab.WEB_PORTAL, "05 / WEB COMPANION", "open_web_drawer")
                        )
                        consoleButtons.forEach { (tab, label, tag) ->
                            Button(
                                onClick = { viewModel.openOperationalDrawer(tab) },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = InkPrimary,
                                    contentColor = AchromaticGround
                                ),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag(tag)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SignalAccent, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.14.em
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                // =========================================================================
                // SECTION 6: RUNTIME FIELD-SWAP PICKER (0.7S COLOR & DATA TRANSITION)
                // =========================================================================
                val animatedFieldGround by animateColorAsState(
                    targetValue = Color(selectedFieldPreset.groundHex),
                    animationSpec = tween(durationMillis = 700),
                    label = "fieldGround"
                )
                val animatedFieldInk by animateColorAsState(
                    targetValue = Color(selectedFieldPreset.inkHex),
                    animationSpec = tween(durationMillis = 700),
                    label = "fieldInk"
                )
                val fieldDividerColor = animatedFieldInk.copy(alpha = 0.24f)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(animatedFieldGround)
                        .padding(horizontal = 20.dp, vertical = 48.dp)
                ) {
                    Text(
                        text = "05 / RUNTIME FIELD-SWAP CALIBRATION",
                        style = MaterialTheme.typography.labelLarge,
                        color = animatedFieldInk.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Reconfigure optical & settlement profiles on the fly.",
                        fontFamily = ArchivoDisplayFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 29.sp,
                        letterSpacing = (-0.04).em,
                        color = animatedFieldInk
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pill buttons carrying data attributes for ground, ink, and values
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        viewModel.fieldSwapPresets.forEach { preset ->
                            val isSelected = preset.id == selectedFieldPreset.id
                            OutlinedButton(
                                onClick = { viewModel.selectFieldSwapPreset(preset) },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) animatedFieldInk else Color.Transparent,
                                    contentColor = if (isSelected) animatedFieldGround else animatedFieldInk
                                ),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("field_preset_${preset.id.lowercase()}")
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(SignalAccent, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = preset.label,
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.14.em
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Three-cell definition panel with 24% currentColor hairline borders
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, fieldDividerColor)
                    ) {
                        val cells = listOf(
                            Triple(selectedFieldPreset.cell1Title, selectedFieldPreset.cell1Value, selectedFieldPreset.cell1Unit),
                            Triple(selectedFieldPreset.cell2Title, selectedFieldPreset.cell2Value, selectedFieldPreset.cell2Unit),
                            Triple(selectedFieldPreset.cell3Title, selectedFieldPreset.cell3Value, selectedFieldPreset.cell3Unit)
                        )
                        cells.forEachIndexed { idx, (title, value, unit) ->
                            if (idx > 0) {
                                HorizontalDivider(thickness = 1.dp, color = fieldDividerColor)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = animatedFieldInk.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = value,
                                        fontFamily = ArchivoDisplayFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 36.sp,
                                        lineHeight = 32.sp,
                                        letterSpacing = (-0.04).em,
                                        color = animatedFieldInk
                                    )
                                }
                                Text(
                                    text = unit,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selectedFieldPreset.groundHex == 0xFF0D0D0FL) SignalAccentLift else SignalAccent
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                // =========================================================================
                // SECTION 7: 12-ROW HAIRLINE SPEC TABLE & CROPPED 'BAZARLY' BOOKEND CLOSE
                // =========================================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AchromaticGround)
                        .padding(top = 48.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            text = "06 / TECHNICAL SPECIFICATION MATRIX",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = " Reference-grade architecture specifications.",
                            fontFamily = ArchivoDisplayFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            lineHeight = 27.sp,
                            letterSpacing = (-0.04).em,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        val specRows = listOf(
                            "Optical Sensor Architecture" to "TELECENTRIC CMOS // 5.0 MP",
                            "Spatial Repeatability" to "0.018 MM RMS",
                            "Line Scan Cadence" to "2,400 STITCHES / MIN",
                            "Cloud Persistence Engine" to "FIREBASE FIRESTORE (ASIA-SE1)",
                            "Security Rules Model" to "ZERO-TRUST UID OWNERSHIP",
                            "Authentication Protocol" to "GOOGLE CREDENTIAL MANAGER",
                            "Multimodal Inference Model" to "GEMINI 3.8 FLASH + APP CHECK",
                            "Voice ERP Languages" to "BANGLA (BN) & ENGLISH (EN)",
                            "Local Payment Rail" to "BKASH TOKENIZED MODE 0011",
                            "Token Grant Single-Flight" to "3,540 SEC MUTEX CACHE",
                            "Global Enterprise Billing" to "STRIPE USD + 10% SAVINGS SHARE",
                            "Active Cloud Records Synced" to "${inspections.size + voiceActions.size + agreements.size} DOCUMENTS"
                        )

                        specRows.forEach { (label, valText) ->
                            HorizontalDivider(thickness = 1.dp, color = HairlineRule)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkSecondary
                                )
                                Text(
                                    text = valText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkPrimary
                                )
                            }
                        }
                        HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                        Spacer(modifier = Modifier.height(40.dp))

                        // Close Section Headline & Right-Pushed Pill Buttons
                        Text(
                            text = "Deploy Bazarly across your production floor.",
                            fontFamily = ArchivoDisplayFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            lineHeight = 30.sp,
                            letterSpacing = (-0.04).em,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "FIREBASE PROJECT // PROJECT-17A70087-2BC2-45B7-91A // ALL SYSTEMS NOMINAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.openOperationalDrawer(NavigationTab.PAYMENTS) },
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "BIND BKASH 0011",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.14.em,
                                    color = InkPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = { viewModel.openOperationalDrawer(NavigationTab.INSPECTION) },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = InkPrimary,
                                    contentColor = AchromaticGround
                                ),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SignalAccent, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "START METROLOGY",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.14.em
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        HorizontalDivider(thickness = 1.dp, color = HairlineRule)

                        // 4-item hairline footer strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("BAZARLY OS v4.2", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                            Text("DHAKA // SINGAPORE", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                            Text("0.018 MM RMS", style = MaterialTheme.typography.labelSmall, color = SignalAccent)
                            Text("FIRESTORE LIVE", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                        }
                    }

                    // Full-width closing BAZARLY bookend wordmark translated down so bottom edge crops it
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp)
                            .clipToBounds()
                    ) {
                        PosterWordmarkLayer(
                            frontOcclusionOnly = false,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .offset(y = 14.dp)
                        )
                    }
                }
            }
        }
    }
}
