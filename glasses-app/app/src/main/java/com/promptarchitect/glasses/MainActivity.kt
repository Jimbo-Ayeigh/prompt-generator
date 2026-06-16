package com.promptarchitect.glasses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.fonts.createGoogleSansFlexTypography

/**
 * Single-activity entry point for the AI Glasses build of Prompt Architect.
 *
 * The whole app runs inside [GlimmerTheme], which adapts colors, depth and typography
 * for transparent additive displays. Navigation is intentionally lightweight (a small
 * screen state machine) so the focus-based input model stays simple.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PromptArchitectApp() }
    }
}

@Composable
fun PromptArchitectApp() {
    // Google Sans Flex is the recommended variable font for legibility on glasses.
    val typography = createGoogleSansFlexTypography()
    GlimmerTheme(typography = typography) {
        AppRoot()
    }
}

/** The screens this app can show. Kept as a sealed hierarchy for exhaustive routing. */
private sealed interface Screen {
    data object Home : Screen
    data object Blueprints : Screen
    data class Detail(val blueprint: SongBlueprint) : Screen
    data object Chaining : Screen
}

@Composable
private fun AppRoot() {
    var screen: Screen by remember { mutableStateOf(Screen.Home) }

    when (val current = screen) {
        Screen.Home -> HomeScreen(
            onOpenBlueprints = { screen = Screen.Blueprints },
            onOpenChaining = { screen = Screen.Chaining },
        )

        Screen.Blueprints -> BlueprintListScreen(
            blueprints = SampleData.blueprints,
            onSelect = { screen = Screen.Detail(it) },
            onBack = { screen = Screen.Home },
        )

        is Screen.Detail -> BlueprintDetailScreen(
            blueprint = current.blueprint,
            onBack = { screen = Screen.Blueprints },
        )

        Screen.Chaining -> ChainingGuideScreen(
            onBack = { screen = Screen.Home },
        )
    }
}
