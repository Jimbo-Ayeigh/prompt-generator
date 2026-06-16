package com.pricelens.glasses

/** An object the glasses have recognised in the wearer's field of view. */
data class RecognizedObject(
    val id: String,
    val label: String,
    val confidence: Float, // 0f..1f
)

/** Pricing for a recognised object, resolved asynchronously from a pricing source. */
sealed interface PriceState {
    data object Loading : PriceState
    data class Available(val formattedPrice: String, val source: String) : PriceState
    data class Unavailable(val reason: String) : PriceState
}

/** A recognised object together with its (possibly still loading) price. */
data class PricedObject(
    val recognized: RecognizedObject,
    val price: PriceState,
)

/** Top-level UI state for the live scanner. */
sealed interface ScanState {
    data object Searching : ScanState
    data class Identified(val item: PricedObject) : ScanState
}
