package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TelemetryKpi
import com.example.ui.theme.AuralisAmber
import com.example.ui.theme.AuralisCyan
import com.example.ui.theme.AuralisGreen
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.viewmodel.AppLanguage
import org.json.JSONObject

@Composable
fun GrantTelemetryScreen(
    language: AppLanguage,
    telemetry: TelemetryKpi,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BN
    var showExportedJson by remember { mutableStateOf(false) }

    val auditJson = remember(telemetry) {
        """
        {
          "governanceFramework": "Startup Bangladesh Limited (SBL) & iDEA Project",
          "grantVehicle": "iDEA Pre-Seed Grant (BDT 10 Lakh)",
          "applicantEntity": "Auralis AI Operations Technologies Ltd",
          "bangladeshiOwnershipPercent": 100,
          "activeFactories": ${telemetry.activeFactories},
          "activeMerchants": ${telemetry.activeDokans},
          "defectReductionPercent": ${telemetry.defectReductionPercent},
          "grossMerchandiseValueBdt": ${telemetry.gmvTotalBdt},
          "monthlyRecurringRevenueUsd": ${telemetry.monthlyRecurringRevenueUsd},
          "netRevenueRetentionPercent": ${telemetry.netRevenueRetentionPercent},
          "ltvToCacRatio": ${telemetry.ltvCacRatio},
          "scope3CarbonSavedKg": ${telemetry.scope3CarbonSavedKg},
          "ideaTranche1Status": "100% KPI AUDITED & RECOMMENDED",
          "sblGovernanceComplianceScore": "96/100"
        }
        """.trimIndent()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = if (isBn) "আইডিয়া অনুদান ও এসবিএল গভর্নেন্স টেলিমেট্রি" else "iDEA Grant & SBL Governance Telemetry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isBn) "ট্রাঞ্চ অডিট এবং প্রাতিষ্ঠানিক ভিসি কমপ্লায়েন্স ট্র্যাকার" else "Performance Monitoring Committee (PMC) tranche audit readiness",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // iDEA Project Pre-Seed Grant Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("idea_grant_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F231D)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuralisGreen.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AuralisGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "iDEA Pre-Seed Grant (BCC / ICT Division)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AuralisGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "৳10 LAKH APPROVED",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AuralisGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isBn)
                            "১০০% স্থানীয় মালিকানা, ৩ জন তরুণ কো-ফাউন্ডার এবং কার্যকরী এমভিপি মানদণ্ড পূরণ করায় ১ম ট্রাঞ্চ অনুমোদিত।"
                        else
                            "Standard Operating Procedure (SOP) compliance: 51%+ Bangladeshi ownership, 3-founder technical team, and active MVP validation verified by PMT.",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SMART KPI Tranche 1 Milestone",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "100% DISBURSED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = AuralisGreen
                        )
                    }
                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AuralisGreen,
                        trackColor = Color(0xFF091410)
                    )
                }
            }
        }

        // Key Institutional VC Metrics Grid (SBL Focus)
        item {
            Text(
                text = if (isBn) "স্টার্টআপ বাংলাদেশ লিমিটেড (SBL) গভর্নেন্স ম্যাট্রিক্স" else "SBL Sovereign VC & Governance Metrics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = if (isBn) "সক্রিয় ফ্যাক্টরি" else "Active Factories",
                    value = "${telemetry.activeFactories} Plants",
                    subtext = "+84 SME Dokans",
                    accentColor = AuralisCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (isBn) "ডিফেক্ট হ্রাস" else "Defect Reduction",
                    value = "${telemetry.defectReductionPercent}%",
                    subtext = "Goal: >30%",
                    accentColor = AuralisAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = if (isBn) "মোট জিএমভি" else "Gross GMV",
                    value = "৳1.25 Cr",
                    subtext = "MFS + B2B Invoices",
                    accentColor = AuralisGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (isBn) "নেট রিটেনশন" else "Net Retention (NRR)",
                    value = "${telemetry.netRevenueRetentionPercent}%",
                    subtext = "LTV:CAC 4.6x",
                    accentColor = Color(0xFF818CF8),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Export Structured Audit JSON Button
        item {
            Button(
                onClick = { showExportedJson = !showExportedJson },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("export_audit_json_button"),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuralisCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = AuralisCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showExportedJson)
                        (if (isBn) "অডিট ফাইল লুকিয়ে ফেলুন" else "Hide Audit JSON")
                    else
                        (if (isBn) "iDEA / SBL অডিট JSON এক্সপোর্ট করুন" else "Export iDEA / SBL Audit JSON"),
                    color = AuralisCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Structured JSON View
        if (showExportedJson) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXPORTED TELEMETRY ARTIFACT (DELIVERABLE 4)",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AuralisCyan
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AuralisGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = auditJson,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtext, fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}
