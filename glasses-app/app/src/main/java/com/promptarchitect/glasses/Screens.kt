package com.promptarchitect.glasses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.VerticalList

/**
 * Top-level menu. Glimmer guidance is to show three focusable items or fewer per view,
 * so the home menu stays short and scannable.
 */
@Composable
fun HomeScreen(
    onOpenBlueprints: () -> Unit,
    onOpenChaining: () -> Unit,
) {
    VerticalList(
        title = { TitleChip { Text("Prompt Architect") } },
    ) {
        item {
            ListItem(
                onClick = onOpenBlueprints,
                leadingIcon = { Icon(Icons.Filled.LibraryMusic, contentDescription = null) },
            ) {
                Text("Song Blueprints")
            }
        }
        item {
            ListItem(
                onClick = onOpenChaining,
                leadingIcon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
            ) {
                Text("Prompt Chaining")
            }
        }
    }
}

/** Browsable list of saved blueprints; selecting one opens its detail card. */
@Composable
fun BlueprintListScreen(
    blueprints: List<SongBlueprint>,
    onSelect: (SongBlueprint) -> Unit,
    onBack: () -> Unit,
) {
    VerticalList(
        title = { TitleChip { Text("Song Blueprints") } },
    ) {
        items(blueprints) { blueprint ->
            ListItem(
                onClick = { onSelect(blueprint) },
                leadingIcon = { Icon(Icons.Filled.MusicNote, contentDescription = null) },
            ) {
                Column {
                    Text(blueprint.title, style = GlimmerTheme.typography.titleLarge)
                    Text(
                        "${blueprint.genre} · ${blueprint.emotion}",
                        style = GlimmerTheme.typography.bodySmall,
                    )
                }
            }
        }
        item {
            ListItem(
                onClick = onBack,
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
            ) {
                Text("Back")
            }
        }
    }
}

/** A single blueprint shown as a Glimmer Card with a title slot and a back action. */
@Composable
fun BlueprintDetailScreen(
    blueprint: SongBlueprint,
    onBack: () -> Unit,
) {
    Card(
        title = { Text(blueprint.title) },
        action = {
            Button(
                onClick = onBack,
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                },
            ) {
                Text("Back")
            }
        },
    ) {
        Column {
            LabeledValue("Genre", blueprint.genre)
            LabeledValue("Emotion", blueprint.emotion)
            LabeledValue("Tempo", blueprint.tempo)
            LabeledValue("Instruments", blueprint.instruments)
            Spacer(Modifier.height(12.dp))
            Text("Master Prompt", style = GlimmerTheme.typography.titleLarge)
            Text(blueprint.masterPrompt, style = GlimmerTheme.typography.bodySmall)
        }
    }
}

/** Walks through the prompt-chaining technique, one step per focusable item. */
@Composable
fun ChainingGuideScreen(
    onBack: () -> Unit,
) {
    VerticalList(
        title = { TitleChip { Text("Prompt Chaining") } },
    ) {
        item {
            ChainingStep(
                step = "1 · Foundation",
                detail = "Gritty 90s boom-bap drum loop, 90 BPM, vinyl crackle.",
            )
        }
        item {
            ChainingStep(
                step = "2 · Add the bass",
                detail = "Deep sub-bassline with a groovy rhythm that locks into the drum loop.",
            )
        }
        item {
            ChainingStep(
                step = "3 · Layer melody",
                detail = "Melancholic jazzy piano floating over the beat and the sub-bass.",
            )
        }
        item {
            ListItem(
                onClick = onBack,
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun ChainingStep(step: String, detail: String) {
    ListItem {
        Column {
            Text(step, style = GlimmerTheme.typography.titleLarge)
            Text(detail, style = GlimmerTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(label, style = GlimmerTheme.typography.bodySmall)
        Text(value, style = GlimmerTheme.typography.titleLarge)
    }
}
