package com.pricelens.glasses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the live scanner: listens to the recognizer, fetches a price whenever a new
 * object is identified, and exposes a single [ScanState] (plus a short history) for the
 * UI to render. Dependencies are injected so the camera/ML and pricing backends can be
 * swapped without touching the UI.
 */
class ScannerViewModel(
    private val recognizer: ObjectRecognizer = DemoObjectRecognizer(),
    private val pricing: PricingService = DemoPricingService(),
) : ViewModel() {

    private val _state = MutableStateFlow<ScanState>(ScanState.Searching)
    val state: StateFlow<ScanState> = _state.asStateFlow()

    private val _history = MutableStateFlow<List<PricedObject>>(emptyList())
    val history: StateFlow<List<PricedObject>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            recognizer.recognitions().collect { recognized ->
                if (recognized == null) {
                    _state.value = ScanState.Searching
                    return@collect
                }
                // Show the object immediately with a loading price, then resolve it.
                val pending = PricedObject(recognized, PriceState.Loading)
                _state.value = ScanState.Identified(pending)

                val priced = pending.copy(price = pricing.priceFor(recognized))
                _state.value = ScanState.Identified(priced)
                addToHistory(priced)
            }
        }
    }

    private fun addToHistory(item: PricedObject) {
        // Keep the most recent distinct objects; Glimmer lists stay short on glasses.
        _history.value = (listOf(item) + _history.value)
            .distinctBy { it.recognized.id }
            .take(3)
    }

    companion object {
        /** Builds the ViewModel with concrete camera/pricing backends injected. */
        fun factory(
            recognizer: ObjectRecognizer,
            pricing: PricingService,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ScannerViewModel(recognizer, pricing) }
        }
    }
}
