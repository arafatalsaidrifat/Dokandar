package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.authStateFlow
import com.example.auth.configureAppCheck
import com.example.auth.signOut
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BazarlyPosterLandingScreen
import com.example.ui.theme.AchromaticGround
import com.example.ui.theme.AuralisOSTheme
import com.example.ui.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mandatory: Configure Firebase App Check BEFORE any Firebase AI or Firestore call
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            AuralisOSTheme(darkTheme = false) {
                BazarlyMainApp()
            }
        }
    }
}

@Composable
fun BazarlyMainApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val currentUser by remember { Firebase.auth.authStateFlow() }
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    var fallbackSessionUid by remember { mutableStateOf<String?>("dev-operator") }
    val activeUid = currentUser?.uid ?: fallbackSessionUid

    // Bind Firestore real-time listeners (or local dev-operator session) to activeUid
    LaunchedEffect(activeUid) {
        viewModel.bindAuthenticatedUser(activeUid)
    }

    // Request runtime permissions for Camera and Audio
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = AchromaticGround
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeUid == null) {
                AuthScreen(
                    onSignInSuccess = { resolvedUid ->
                        fallbackSessionUid = resolvedUid
                        viewModel.bindAuthenticatedUser(resolvedUid)
                    },
                    onExploreStage = {
                        fallbackSessionUid = "dev-operator"
                        viewModel.bindAuthenticatedUser("dev-operator")
                    },
                    onOpenWebApp = {
                        fallbackSessionUid = "dev-operator"
                        viewModel.bindAuthenticatedUser("dev-operator")
                        viewModel.openOperationalDrawer(com.example.ui.viewmodel.NavigationTab.WEB_PORTAL)
                    }
                )
            } else {
                BazarlyPosterLandingScreen(
                    viewModel = viewModel,
                    onSignOut = {
                        fallbackSessionUid = null
                        signOut(
                            context = context,
                            credentialManager = credentialManager,
                            onSignOutComplete = {
                                viewModel.bindAuthenticatedUser(null)
                            },
                            scope = coroutineScope
                        )
                    }
                )
            }
        }
    }
}
