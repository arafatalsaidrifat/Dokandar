package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAgentEngine
import com.example.data.model.InspectionRecord
import com.example.data.model.ProductionNode
import com.example.data.model.TelemetryKpi
import com.example.ui.theme.AuralisAmber
import com.example.ui.theme.AuralisCyan
import com.example.ui.theme.AuralisGreen
import com.example.ui.theme.AuralisRed
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.viewmodel.AppLanguage

@Composable
fun VisionInspectionScreen(
    language: AppLanguage,
    isInspecting: Boolean,
    activeInspection: InspectionRecord?,
    nodes: List<ProductionNode>,
    telemetry: TelemetryKpi,
    onTriggerScan: () -> Unit,
    onRerouteNode: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BN

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "মডিউল এ: কম্পিউটার ভিশন মেট্রোলজি" else "Module A: Computer Vision Metrology",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isBn) "রিয়েল-টাইম ডিফেক্ট গ্রিড এবং স্বয়ংক্রিয় রি-রুটিং" else "Zero-latency defect classification & autonomous routing",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0E3A53))
                        .border(1.dp, AuralisCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "GEMINI-2.5",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuralisCyan
                    )
                }
            }
        }

        // Metrology Viewport HUD Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("metrology_viewport_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF060911)),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Scanning Line Animation
                    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
                    val scanProgress by infiniteTransition.animateFloat(
                        initialValue = 0.05f,
                        targetValue = 0.95f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scanLineY"
                    )

                    // Draw Metrology Grid and Bounding Box
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Grid lines
                        val stepX = canvasWidth / 10
                        val stepY = canvasHeight / 8
                        for (i in 1..9) {
                            drawLine(
                                color = Color(0x1500E5FF),
                                start = Offset(stepX * i, 0f),
                                end = Offset(stepX * i, canvasHeight),
                                strokeWidth = 1f
                            )
                        }
                        for (j in 1..7) {
                            drawLine(
                                color = Color(0x1500E5FF),
                                start = Offset(0f, stepY * j),
                                end = Offset(canvasWidth, stepY * j),
                                strokeWidth = 1f
                            )
                        }

                        // Center reticle
                        drawCircle(
                            color = Color(0x2200E5FF),
                            radius = 60f,
                            center = Offset(canvasWidth / 2, canvasHeight / 2),
                            style = Stroke(width = 1.5f)
                        )

                        // Laser scan line
                        val laserY = canvasHeight * scanProgress
                        drawLine(
                            brush = Brush.horizontalGradient(
                                listOf(Color.Transparent, AuralisCyan, Color.Transparent)
                            ),
                            start = Offset(0f, laserY),
                            end = Offset(canvasWidth, laserY),
                            strokeWidth = 3f
                        )

                        // Bounding Box from Active Inspection
                        activeInspection?.let { record ->
                            val boxX = record.boxLeft * canvasWidth
                            val boxY = record.boxTop * canvasHeight
                            val boxW = (record.boxRight - record.boxLeft) * canvasWidth
                            val boxH = (record.boxBottom - record.boxTop) * canvasHeight

                            val boxColor = when (record.severity) {
                                "CRITICAL" -> AuralisRed
                                "HIGH" -> AuralisAmber
                                else -> AuralisGreen
                            }

                            // Box rectangle
                            drawRect(
                                color = boxColor.copy(alpha = 0.15f),
                                topLeft = Offset(boxX, boxY),
                                size = Size(boxW, boxH)
                            )
                            drawRect(
                                color = boxColor,
                                topLeft = Offset(boxX, boxY),
                                size = Size(boxW, boxH),
                                style = Stroke(width = 2.5f)
                            )

                            // Corner accents
                            val len = 16f
                            drawLine(boxColor, Offset(boxX, boxY), Offset(boxX + len, boxY), 4f)
                            drawLine(boxColor, Offset(boxX, boxY), Offset(boxX, boxY + len), 4f)
                            drawLine(boxColor, Offset(boxX + boxW, boxY), Offset(boxX + boxW - len, boxY), 4f)
                            drawLine(boxColor, Offset(boxX + boxW, boxY), Offset(boxX + boxW, boxY + len), 4f)
                            drawLine(boxColor, Offset(boxX, boxY + boxH), Offset(boxX + len, boxY + boxH), 4f)
                            drawLine(boxColor, Offset(boxX, boxY + boxH), Offset(boxX, boxY + boxH - len), 4f)
                            drawLine(boxColor, Offset(boxX + boxW, boxY + boxH), Offset(boxX + boxW - len, boxY + boxH), 4f)
                            drawLine(boxColor, Offset(boxX + boxW, boxY + boxH), Offset(boxX + boxW, boxY + boxH - len), 4f)
                        }
                    }

                    // Top Left Telemetry HUD Tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xD00B0F19))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SENSOR 1080p • 60 FPS • 0.05mm",
                            color = AuralisCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Active Inspection Details Overlay Badge
                    activeInspection?.let { insp ->
                        val badgeColor = when (insp.severity) {
                            "CRITICAL" -> AuralisRed
                            "HIGH" -> AuralisAmber
                            else -> AuralisGreen
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (insp.hasDefect) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${(insp.confidenceScore * 100).toInt()}% CONF",
                                    color = badgeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Trigger Scan CTA Button Overlay at Bottom Center
                    Button(
                        onClick = onTriggerScan,
                        enabled = !isInspecting,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                            .testTag("trigger_inspection_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AuralisCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isInspecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "বিশ্লেষণ হচ্ছে..." else "Analyzing Frame...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "নতুন ইন্সপেকশন চালান" else "Trigger Metrology Scan",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Defect Classification & Autonomous Reroute Recommendation Card
        activeInspection?.let { record ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (record.hasDefect) AuralisAmber.copy(alpha = 0.5f) else AuralisGreen.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) record.banglaDefectLabel else record.defectLabel,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (record.hasDefect) AuralisAmber else AuralisGreen
                            )
                            Text(
                                text = record.affectedStation,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn) record.banglaCorrectiveAction else record.correctiveAction,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 18.sp
                        )

                        if (record.hasDefect) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onRerouteNode("NODE-02", "NODE-03") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4D2C00)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AuralisAmber),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("execute_reroute_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = null,
                                    tint = AuralisAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "স্বয়ংক্রিয় রি-রুট অবিলম্বে কার্যকর করুন" else "Execute Autonomous Reroute Now",
                                    color = AuralisAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Factory Production Nodes Grid
        item {
            Text(
                text = if (isBn) "ফ্যাক্টরি লাইন ও মেশিনের স্থিতি" else "Factory Production Stations",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(nodes.size) { index ->
            val node = nodes[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) node.banglaName else node.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${node.lineType} • ${node.completedUnits}/${node.dailyTargetUnits} Units",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        val statusColor = when (node.status) {
                            "OPTIMAL" -> AuralisGreen
                            "DEGRADED" -> AuralisAmber
                            else -> AuralisCyan
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = node.status,
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "দক্ষতা: ${node.efficiencyPercent}%" else "Efficiency: ${node.efficiencyPercent}%",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${node.carbonIndexKg} kg CO2e",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    LinearProgressIndicator(
                        progress = { node.efficiencyPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (node.efficiencyPercent > 80) AuralisCyan else AuralisAmber,
                        trackColor = Color(0xFF0F172A),
                    )
                }
            }
        }

        // Scope-3 ESG Compliance Oracle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2319)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E5B3E))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = AuralisGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "স্কোপ-৩ ইএসজি এবং বায়ার কমপ্লায়েন্স" else "Scope-3 ESG & Buyer Compliance Oracle",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "EU CSDDD / US UFLPA",
                            fontSize = 10.sp,
                            color = AuralisGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = GeminiAgentEngine.generateScope3Summary(nodes.size, telemetry.defectReductionPercent),
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
