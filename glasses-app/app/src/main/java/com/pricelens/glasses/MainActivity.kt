package com.pricelens.glasses

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.fonts.createGoogleSansFlexTypography

/**
 * Single-activity entry point for PriceLens — a live "identify & price" overlay for AI
 * glasses. Everything renders inside [GlimmerTheme]. The scanner only starts once the
 * CAMERA permission is granted.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PriceLensApp() }
    }
}

@Composable
fun PriceLensApp() {
    // Google Sans Flex is the recommended variable font for legibility on glasses.
    val typography = createGoogleSansFlexTypography()
    GlimmerTheme(typography = typography) {
        val context = LocalContext.current
        var hasCamera by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED,
            )
        }
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted -> hasCamera = granted }

        if (hasCamera) {
            ScannerRoute()
        } else {
            CameraPermissionCard(
                onEnable = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            )
        }
    }
}

@Composable
private fun ScannerRoute() {
    val recognizer = rememberCameraRecognizer()
    val pricing = remember { SerpApiPricingService(BuildConfig.SERPAPI_KEY) }
    val scannerViewModel: ScannerViewModel = viewModel(
        factory = ScannerViewModel.factory(recognizer, pricing),
    )
    ScannerScreen(scannerViewModel)
}

@Composable
private fun CameraPermissionCard(onEnable: () -> Unit) {
    Card(
        title = { Text("Camera needed") },
        action = { Button(onClick = onEnable) { Text("Enable") } },
    ) {
        Text(
            "PriceLens uses the camera to identify objects in view.",
            style = GlimmerTheme.typography.bodySmall,
        )
    }
}
