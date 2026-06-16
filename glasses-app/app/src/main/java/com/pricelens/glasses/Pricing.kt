package com.pricelens.glasses

import kotlinx.coroutines.delay

/**
 * Resolves an estimated market price for a recognised object.
 *
 * A real implementation would call a pricing / marketplace API (eBay, StockX, a price
 * comparison service, etc.) keyed off the recognised label or a scanned barcode, ideally
 * with a short-lived cache. Keep it off the main thread — it is a `suspend` function.
 */
interface PricingService {
    suspend fun priceFor(item: RecognizedObject): PriceState
}

/** Demo pricing with canned values so the UI shows realistic numbers offline. */
class DemoPricingService : PricingService {
    private val catalog = mapOf(
        "nike-af1" to ("$110" to "Retail"),
        "psa-charizard" to ("$420" to "eBay · 30-day avg"),
        "kitchenaid" to ("$379" to "MSRP"),
        "eames-chair" to ("$5,495" to "Herman Miller"),
    )

    override suspend fun priceFor(item: RecognizedObject): PriceState {
        delay(600) // simulate a network round-trip
        val match = catalog[item.id] ?: return PriceState.Unavailable("No price found")
        return PriceState.Available(formattedPrice = match.first, source = match.second)
    }
}
