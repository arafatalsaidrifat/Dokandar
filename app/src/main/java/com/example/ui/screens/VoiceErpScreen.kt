package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceAction
import com.example.ui.theme.AuralisAmber
import com.example.ui.theme.AuralisCyan
import com.example.ui.theme.AuralisGreen
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.viewmodel.AppLanguage

@Composable
fun VoiceErpScreen(
    language: AppLanguage,
    isListening: Boolean,
    inputText: String,
    latestResult: VoiceAction?,
    allActions: List<VoiceAction>,
    onInputChange: (String) -> Unit,
    onToggleListening: () -> Unit,
    onSubmitCommand: () -> Unit,
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
            Column {
                Text(
                    text = if (isBn) "মডিউল বি: বাংলা/ইংরেজি ভয়েস-টু-অ্যাকশন ইআরপি" else "Module B: Bilingual Voice ERP Agent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isBn) "ভয়েস নির্দেশে ইনভয়েস তৈরি, রিস্টক ও মেশিন রুট পরিবর্তন" else "Natural voice execution via Gemini Function Calling",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Voice Microphone Console Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_console_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isListening) AuralisCyan else DarkOutline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Microphone Button
                    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = if (isListening) 1.25f else 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color(0xFF1E293B))
                            .border(2.dp, if (isListening) AuralisCyan else Color(0xFF334155), CircleShape)
                            .clickable { onToggleListening() }
                            .testTag("microphone_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Voice Input",
                            tint = if (isListening) AuralisCyan else Color(0xFF94A3B8),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isListening) {
                            if (isBn) "শুনছি... আপনার কমান্ড বলুন" else "Listening... Speak your ERP command"
                        } else {
                            if (isBn) "ভয়েস কমান্ড দিতে মাইক চাপুন" else "Tap microphone to speak or enter command below"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isListening) AuralisCyan else Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Spoken/Text Command Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_command_input"),
                        placeholder = {
                            Text(
                                text = if (isBn) "যেমন: লাইন ৩ এর জন্য ৫০ গজ সুতি কাপড় রিস্টক করো" else "e.g. Create invoice for 500 polo shirts at 450 BDT",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFE2E8F0),
                            focusedBorderColor = AuralisCyan,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF090D16),
                            unfocusedContainerColor = Color(0xFF090D16)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            Button(
                                onClick = onSubmitCommand,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .testTag("submit_voice_command_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AuralisCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Execute",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PresetChip(
                            label = if (isBn) "🇧🇩 ইনভয়েস তৈরি" else "Invoice Generator",
                            onClick = {
                                onInputChange(if (isBn) "মিরপুর টেক্সটাইলের জন্য ৫০০ পিস পোলো শার্ট ইনভয়েস তৈরি করো" else "Create invoice for 500 polo shirts at 450 BDT")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            label = if (isBn) "📦 সুতা রিস্টক" else "Restock Yarn",
                            onClick = {
                                onInputChange(if (isBn) "লাইন ৩ এর জন্য ৫০ গজ সুতি কাপড় রিস্টক অর্ডার করো" else "Restock 250 bales carded cotton yarn from Square Textiles")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            label = if (isBn) "⚙️ স্টেশন রি-রুট" else "Reroute Station",
                            onClick = {
                                onInputChange(if (isBn) "লাইন ২ ব্রেকডাউন - রুট লাইন ৩ এ ট্রান্সফার করো" else "Reroute station 2 to station 3 hemmer")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Live Function Calling JSON Execution Inspector
        latestResult?.let { result ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuralisCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = AuralisCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GEMINI FUNCTION CALLING TOOL: ${result.intent}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AuralisCyan
                                )
                            }
                            Text(
                                text = "STATUS: ${result.status}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = AuralisGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = result.structuredJson,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF93C5FD),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Recent Spoken Actions Feed
        item {
            Text(
                text = if (isBn) "সাম্প্রতিক স্বয়ংক্রিয় একশন লগ" else "Recent Autonomous Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(allActions) { action ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = action.transcript,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Intent: ${action.intent} • Lang: ${action.language.uppercase()}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuralisGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = action.status,
                            color = AuralisGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFCBD5E1)
        )
    }
}
