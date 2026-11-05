package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.AchromaticGround
import com.example.ui.theme.DimmerStageGround
import com.example.ui.theme.HairlineRule
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.SignalAccent
import com.example.ui.viewmodel.AppLanguage

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebCompanionScreen(
    language: AppLanguage,
    portalUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BN

    var webViewRef: WebView? = remember { null }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AchromaticGround)
    ) {
        // Top Technical Web Server Status Bar (0 border-radius, 1px hairline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DimmerStageGround)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("web_companion_header"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(SignalAccent, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isBn) "বাজারলি ওয়েব পোর্টাল সার্ভার সক্রিয়" else "BAZARLY WEB PORTAL SERVER ACTIVE",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkPrimary
                    )
                    Text(
                        text = portalUrl.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { webViewRef?.reload() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = InkPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open in Browser",
                        tint = SignalAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = HairlineRule)

        // Seamless In-App Responsive Web Portal Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, HairlineRule)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("embedded_web_portal_view"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            allowFileAccess = true
                        }
                        setBackgroundColor(0xFFEFEFEE.toInt())
                        loadUrl("file:///android_asset/web/index.html")
                        webViewRef = this
                    }
                },
                update = { view ->
                    webViewRef = view
                }
            )
        }
    }
}
