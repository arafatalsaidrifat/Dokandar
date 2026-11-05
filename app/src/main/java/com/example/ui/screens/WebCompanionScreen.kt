package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.IbmPlexMonoFamily
import com.example.ui.viewmodel.AppLanguage

private class BazarlyWebBridge(
    private val mainHandler: Handler,
    private val onDownloadTriggered: () -> Unit,
    private val onShareTriggered: () -> Unit,
    private val onOpenInChromeTriggered: () -> Unit
) {
    @JavascriptInterface
    fun onDownloadApkRequested() {
        mainHandler.post { onDownloadTriggered() }
    }

    @JavascriptInterface
    fun onShareApkRequested() {
        mainHandler.post { onShareTriggered() }
    }

    @JavascriptInterface
    fun openInChromeBrowser() {
        mainHandler.post { onOpenInChromeTriggered() }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebCompanionScreen(
    language: AppLanguage,
    portalUrl: String,
    apkSizeMb: String = "18.4 MB",
    apkSha256: String = "446962C6...B7E45F7D",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BN
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var webViewRef: WebView? = remember { null }
    var downloadBannerVisible by remember { mutableStateOf(false) }

    val localChromeUrl = "http://127.0.0.1:8080"
    val apkEndpointUrl = "$portalUrl/download/bazarly.apk"
    val emeraldAccent = Color(0xFF34D399)
    val darkBg = Color(0xFF09090B)
    val darkSurface = Color(0xFF18181B)
    val subtleBorder = Color(0x22FFFFFF)

    val launchChromeBrowser = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(localChromeUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    val shareApkDetails = {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Bazarly Universal Android APK (v4.2.0)")
            putExtra(
                Intent.EXTRA_TEXT,
                "Download Bazarly v4.2.0 Universal APK ($apkSizeMb, SHA-256: $apkSha256): $apkEndpointUrl"
            )
        }
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share Bazarly APK Link"))
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Browser Address & Cloud Hosting Bar (Glassmorphic Noir + Emerald)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkBg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("web_companion_header"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Browser URL Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .background(darkSurface, RoundedCornerShape(999.dp))
                    .border(1.dp, subtleBorder, RoundedCornerShape(999.dp))
                    .clickable { launchChromeBrowser() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(emeraldAccent, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isBn) "https://bazarly.vercel.app (লাইভ ওয়েব)" else "https://bazarly.vercel.app // 127.0.0.1:8080",
                        fontFamily = IbmPlexMonoFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 0.04.em,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "TAP TO OPEN IN CHROME BROWSER ↗ // VERCEL & NETLIFY READY",
                        fontFamily = IbmPlexMonoFamily,
                        fontSize = 8.sp,
                        letterSpacing = 0.04.em,
                        color = emeraldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // "GET APP ↓" Emerald-Highlighted Pill Button
                Button(
                    onClick = {
                        downloadBannerVisible = true
                        webViewRef?.evaluateJavascript("openGetAppModal();", null)
                    },
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = emeraldAccent,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("web_top_get_app_button")
                ) {
                    Text(
                        text = "GET APP ↓",
                        fontFamily = IbmPlexMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        letterSpacing = 0.06.em
                    )
                }

                // Open in Chrome Browser Button
                IconButton(
                    onClick = launchChromeBrowser,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("open_in_chrome_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open in Chrome Browser",
                        tint = emeraldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { webViewRef?.reload() },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = subtleBorder)

        // Native APK Export & Verification Banner when "GET APP" is triggered
        if (downloadBannerVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF064E3B))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SIGNED APK STREAMING // Bazarly-v4.2-universal.apk ($apkSizeMb)",
                        fontFamily = IbmPlexMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "SHA-256: $apkSha256 // $apkEndpointUrl",
                        fontFamily = IbmPlexMonoFamily,
                        fontSize = 8.5.sp,
                        color = emeraldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = shareApkDetails,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "SHARE APK",
                        fontFamily = IbmPlexMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = subtleBorder)
        }

        // Full-Screen Glassmorphic Web Application View
        Box(
            modifier = Modifier.fillMaxSize()
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
                        webViewClient = object : WebViewClient() {
                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                if (failingUrl?.startsWith("http://127.0.0.1") == true) {
                                    view?.loadUrl("file:///android_asset/web/index.html")
                                }
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onPermissionRequest(request: PermissionRequest?) {
                                request?.grant(request.resources)
                            }
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            allowFileAccess = true
                            mediaPlaybackRequiresUserGesture = false
                        }
                        addJavascriptInterface(
                            BazarlyWebBridge(
                                mainHandler = mainHandler,
                                onDownloadTriggered = { downloadBannerVisible = true },
                                onShareTriggered = shareApkDetails,
                                onOpenInChromeTriggered = launchChromeBrowser
                            ),
                            "BazarlyNativeBridge"
                        )
                        setDownloadListener { _, _, _, _, _ ->
                            downloadBannerVisible = true
                        }
                        setBackgroundColor(0xFF000000.toInt())
                        loadUrl(localChromeUrl)
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
