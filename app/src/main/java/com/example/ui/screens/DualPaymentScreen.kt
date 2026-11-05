package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.model.PaymentAgreement
import com.example.payments.DualMarketPaymentEngine
import com.example.ui.theme.AuralisAmber
import com.example.ui.theme.AuralisBkashPink
import com.example.ui.theme.AuralisCyan
import com.example.ui.theme.AuralisGreen
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.PaymentRail

@Composable
fun DualPaymentScreen(
    language: AppLanguage,
    activeRail: PaymentRail,
    bindingStep: DualMarketPaymentEngine.BkashBindingStep,
    agreements: List<PaymentAgreement>,
    feedbackMessage: String?,
    onSelectRail: (PaymentRail) -> Unit,
    onStartBinding: () -> Unit,
    onSubmitWallet: (String, String, Double) -> Unit,
    onSubmitOtp: (String, String) -> Unit,
    onSubmitPin: (String, String, String, Double) -> Unit,
    onCancelBinding: () -> Unit,
    onTriggerAutoPay: (String, Double) -> Unit,
    onDismissFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BN

    var walletNumber by remember { mutableStateOf("01723-994120") }
    var otpCode by remember { mutableStateOf("123456") }
    var pinCode by remember { mutableStateOf("•••••") }
    var customerName by remember { mutableStateOf("Apex Garments Ltd (Dhaka EPZ)") }
    var amountBdt by remember { mutableStateOf(25000.0) }

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
                    text = if (isBn) "ডুয়েল-মার্কেট টোকেনাইজড পেমেন্ট গেটওয়ে" else "Dual-Market Tokenized Payment Engine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isBn) "বিকাশ টোকেনাইজড অটো-পে (০MD11) এবং স্ট্রাইপ গ্লোবাল বিলিং" else "bKash Tokenized Checkout (v1.2.0-beta) & Global Stripe SaaS",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Rail Switcher Tabs (bKash BDT vs Stripe USD)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkOutline, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // bKash Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeRail == PaymentRail.BKASH_BDT) AuralisBkashPink else Color.Transparent)
                        .clickable { onSelectRail(PaymentRail.BKASH_BDT) }
                        .padding(vertical = 10.dp)
                        .testTag("tab_bkash_rail"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = if (activeRail == PaymentRail.BKASH_BDT) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "বিকাশ টোকেনাইজড (BDT)" else "bKash Tokenized (BDT)",
                            color = if (activeRail == PaymentRail.BKASH_BDT) Color.White else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stripe Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeRail == PaymentRail.STRIPE_USD) Color(0xFF6366F1) else Color.Transparent)
                        .clickable { onSelectRail(PaymentRail.STRIPE_USD) }
                        .padding(vertical = 10.dp)
                        .testTag("tab_stripe_rail"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = if (activeRail == PaymentRail.STRIPE_USD) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "স্ট্রাইপ গ্লোবাল (USD)" else "Stripe Global (USD)",
                            color = if (activeRail == PaymentRail.STRIPE_USD) Color.White else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Optional Feedback Toast Card
        feedbackMessage?.let { msg ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3822)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuralisGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AuralisGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg, fontSize = 12.sp, color = Color.White)
                        }
                        Text(
                            text = "✕",
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.clickable { onDismissFeedback() }
                        )
                    }
                }
            }
        }

        // bKash Rail Content
        if (activeRail == PaymentRail.BKASH_BDT) {
            // bKash Architecture Spec Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF280718)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuralisBkashPink.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "bKash Tokenized Checkout API (v1.2.0-beta)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AuralisBkashPink
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AuralisBkashPink.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MODE: 0011 / AGREEMENT",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBn)
                                "গ্রাহক একবার ওয়ালেট, ওটিপি ও পিন দিয়ে অ্যাগ্রিমেন্ট তৈরি করবেন। পরবর্তী প্রতি মাসে কোনো ওটিপি ছাড়াই স্বয়ংক্রিয় রিকারিং ডেবিট সম্পন্ন হবে।"
                            else
                                "One-click wallet agreement binding. Generates a persistent agreementId for scheduled 30-day recurring debits without checkout drop-off.",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Interactive Binding Workflow
            when (val step = bindingStep) {
                is DualMarketPaymentEngine.BkashBindingStep.Idle -> {
                    item {
                        Button(
                            onClick = onStartBinding,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_bkash_binding_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AuralisBkashPink),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "+ নতুন বিকাশ ওয়ালেট বাইন্ড করুন (অটো-পে)" else "+ Bind New bKash Wallet (Auto-Pay Agreement)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                is DualMarketPaymentEngine.BkashBindingStep.WalletInput -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuralisBkashPink)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isBn) "ধাপ ১: বিকাশ অ্যাকাউন্ট নম্বর ও সাবস্ক্রিপশন" else "Step 1: bKash Wallet Number & Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = walletNumber,
                                    onValueChange = { walletNumber = it },
                                    label = { Text("bKash MSISDN (11-Digits)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = AuralisBkashPink
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = customerName,
                                    onValueChange = { customerName = it },
                                    label = { Text("Subscriber / Factory Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = AuralisBkashPink
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onCancelBinding,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Cancel", color = Color.White)
                                    }
                                    Button(
                                        onClick = { onSubmitWallet(walletNumber, customerName, amountBdt) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AuralisBkashPink),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("proceed_to_otp_button")
                                    ) {
                                        Text(text = if (isBn) "ওটিপি পাঠান" else "Request OTP", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                is DualMarketPaymentEngine.BkashBindingStep.OtpVerification -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuralisAmber)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isBn) "ধাপ ২: এসএমএস ওটিপি যাচাই (৬ ডিজিট)" else "Step 2: Enter 6-Digit SMS OTP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Sent to ${step.msisdn} via bKash SMS Gateway",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { otpCode = it },
                                    label = { Text("SMS Verification Code") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = AuralisAmber
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onCancelBinding,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Cancel", color = Color.White)
                                    }
                                    Button(
                                        onClick = { onSubmitOtp(step.msisdn, otpCode) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AuralisAmber),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("verify_otp_button")
                                    ) {
                                        Text(text = if (isBn) "ওটিপি ভেরিফাই" else "Verify OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                is DualMarketPaymentEngine.BkashBindingStep.PinConfirmation -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuralisGreen)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isBn) "ধাপ ৩: বিকাশ গোপন পিন নিশ্চিতকরণ" else "Step 3: bKash Secret PIN Confirmation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "This PIN authorizes initial agreement creation only. Stored server-side securely.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = pinCode,
                                    onValueChange = { pinCode = it },
                                    label = { Text("bKash Secret PIN") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = AuralisGreen
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onCancelBinding,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Cancel", color = Color.White)
                                    }
                                    Button(
                                        onClick = { onSubmitPin(step.msisdn, pinCode, customerName, amountBdt) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AuralisGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("confirm_and_bind_button")
                                    ) {
                                        Text(text = if (isBn) "অ্যাগ্রিমেন্ট বাইন্ড করুন" else "Bind Agreement", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }

            // Stored Tokenized Agreements List
            item {
                Text(
                    text = if (isBn) "সংরক্ষিত বিকাশ টোকেনাইজড সাবস্ক্রিপশন" else "Active Tokenized Auto-Pay Agreements",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(agreements) { agreement ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = agreement.payerReference,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "ID: ${agreement.agreementId} • MSISDN: ${agreement.customerMsisdn}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AuralisGreen.copy(alpha = 0.15f))
                                    .border(1.dp, AuralisGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = agreement.status,
                                    color = AuralisGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "৳${agreement.amountBdt.toInt()} / ${agreement.recurringCycle}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuralisBkashPink,
                                fontFamily = FontFamily.Monospace
                            )
                            Button(
                                onClick = { onTriggerAutoPay(agreement.agreementId, agreement.amountBdt) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A1028)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AuralisBkashPink),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = AuralisBkashPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBn) "অটো-পে এক্সিকিউট" else "Run Auto-Pay",
                                    fontSize = 11.sp,
                                    color = AuralisBkashPink,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Stripe Global Multi-Currency Rail Content
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stripe Global Enterprise Multi-Currency Rail",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFA5B4FC)
                            )
                            Text(
                                text = "USD / EUR / GBP",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF818CF8)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBn)
                                "আন্তর্জাতিক রিটেইল ব্র্যান্ডদের জন্য টায়ারড এসএএএস এবং ১০% ইউটিলিটি/ডিফেক্ট শেয়ারড সেভিংস মডেল।"
                            else
                                "Designed for international buyers (H&M, Inditex, PVH) with tiered monthly billing + 10% performance shared savings on defect reduction.",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Tier 1: Factory Pro
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Auralis Supplier Pro", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(text = "$499 / mo", fontWeight = FontWeight.Bold, color = AuralisCyan, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        }
                        Text(text = "Up to 5 Factory Lines • Live Vision Metrology • Bangla/English Voice", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { /* Simulated Stripe checkout */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Subscribe via Stripe USD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tier 2: Global Enterprise
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Enterprise Global Grid", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(text = "$1,899 / mo", fontWeight = FontWeight.Bold, color = Color(0xFFA5B4FC), fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        }
                        Text(text = "Unlimited Supply Plants • Scope-3 ESG Audits • 10% Shared Savings Hook", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { /* Simulated Stripe checkout */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Activate Enterprise Multi-Currency", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
