package com.pricelens.glasses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.fonts.createGoogleSansFlexTypography

/**
 * Single-activity entry point for PriceLens — a live "identify & price" overlay for
 * AI glasses. Everything renders inside [GlimmerTheme], which adapts colors, depth and
 * typography for transparent additive displays.
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
        ScannerScreen()
    }
}
