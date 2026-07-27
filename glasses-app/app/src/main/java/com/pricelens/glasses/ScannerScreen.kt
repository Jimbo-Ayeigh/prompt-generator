package com.pricelens.glasses

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sell
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.VerticalList

/**
 * The live scanner overlay. The top card always reflects what is in view right now;
 * a short "recent" list keeps the last few identified objects within reach.
 */
@Composable
fun ScannerScreen(viewModel: ScannerViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    VerticalList(
        title = { TitleChip { Text("PriceLens") } },
    ) {
        item {
            when (val current = state) {
                ScanState.Searching -> SearchingCard()
                is ScanState.Identified -> IdentifiedCard(current.item)
            }
        }
        if (history.isNotEmpty()) {
            item { Text("Recent", style = GlimmerTheme.typography.bodySmall) }
            items(history) { item -> HistoryRow(item) }
        }
    }
}

@Composable
private fun SearchingCard() {
    Card(title = { Text("Searching…") }) {
        Text("Point at an object to identify and price it.", style = GlimmerTheme.typography.bodySmall)
    }
}

@Composable
private fun IdentifiedCard(item: PricedObject) {
    Card(title = { Text(item.recognized.label) }) {
        Column {
            Text(priceText(item.price), style = GlimmerTheme.typography.titleLarge)
            Text(priceSubtext(item), style = GlimmerTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun HistoryRow(item: PricedObject) {
    ListItem(leadingIcon = { Icon(Icons.Filled.Sell, contentDescription = null) }) {
        Column {
            Text(item.recognized.label, style = GlimmerTheme.typography.titleLarge)
            Text(priceText(item.price), style = GlimmerTheme.typography.bodySmall)
        }
    }
}

private fun priceText(price: PriceState): String = when (price) {
    PriceState.Loading -> "Pricing…"
    is PriceState.Available -> price.formattedPrice
    is PriceState.Unavailable -> "—"
}

private fun priceSubtext(item: PricedObject): String {
    val confidence = "${(item.recognized.confidence * 100).toInt()}% match"
    return when (val price = item.price) {
        PriceState.Loading -> confidence
        is PriceState.Available -> "${price.source} · $confidence"
        is PriceState.Unavailable -> "${price.reason} · $confidence"
    }
}
