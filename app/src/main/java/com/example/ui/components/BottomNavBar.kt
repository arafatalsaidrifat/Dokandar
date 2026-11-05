package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuralisCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.NavigationTab

@Composable
fun BottomNavBar(
    selectedTab: NavigationTab,
    language: AppLanguage,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BN

    NavigationBar(
        modifier = modifier
            .testTag("bottom_nav_bar"),
        containerColor = DarkSurface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        // Tab 1: Vision Metrology
        NavigationBarItem(
            selected = selectedTab == NavigationTab.INSPECTION,
            onClick = { onTabSelected(NavigationTab.INSPECTION) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Metrology",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isBn) "মেট্রোলজি" else "Vision",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0A0F1D),
                selectedTextColor = AuralisCyan,
                indicatorColor = AuralisCyan,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("tab_inspection")
        )

        // Tab 2: Voice ERP
        NavigationBarItem(
            selected = selectedTab == NavigationTab.VOICE_ERP,
            onClick = { onTabSelected(NavigationTab.VOICE_ERP) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice ERP",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isBn) "ভয়েস ইআরপি" else "Voice ERP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0A0F1D),
                selectedTextColor = AuralisCyan,
                indicatorColor = AuralisCyan,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("tab_voice_erp")
        )

        // Tab 3: Payments
        NavigationBarItem(
            selected = selectedTab == NavigationTab.PAYMENTS,
            onClick = { onTabSelected(NavigationTab.PAYMENTS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = "Payments",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isBn) "পেমেন্ট" else "Payments",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0A0F1D),
                selectedTextColor = AuralisCyan,
                indicatorColor = AuralisCyan,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("tab_payments")
        )

        // Tab 4: Telemetry
        NavigationBarItem(
            selected = selectedTab == NavigationTab.TELEMETRY,
            onClick = { onTabSelected(NavigationTab.TELEMETRY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.QueryStats,
                    contentDescription = "Telemetry",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isBn) "টেলিমেট্রি" else "Telemetry",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0A0F1D),
                selectedTextColor = AuralisCyan,
                indicatorColor = AuralisCyan,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("tab_telemetry")
        )

        // Tab 5: Web Companion Portal
        NavigationBarItem(
            selected = selectedTab == NavigationTab.WEB_PORTAL,
            onClick = { onTabSelected(NavigationTab.WEB_PORTAL) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Web Portal",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isBn) "ওয়েব" else "Web",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0A0F1D),
                selectedTextColor = AuralisCyan,
                indicatorColor = AuralisCyan,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("tab_web_portal")
        )
    }
}
