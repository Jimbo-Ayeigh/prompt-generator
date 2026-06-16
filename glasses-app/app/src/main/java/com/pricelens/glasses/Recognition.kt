package com.pricelens.glasses

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Produces a stream of objects recognised in the camera feed.
 *
 * Swap [DemoObjectRecognizer] for a real implementation backed by the glasses camera
 * (CameraX / the Jetpack XR camera APIs) feeding frames into an on-device ML model
 * (e.g. ML Kit Object Detection / Image Labeling, or a custom TFLite classifier).
 * Emit a new [RecognizedObject] only when the object in view changes, so the overlay
 * doesn't flicker on every frame.
 */
interface ObjectRecognizer {
    /** Emits null while nothing confident is in view, or the current best match. */
    fun recognitions(): Flow<RecognizedObject?>
}

/**
 * Stand-in recognizer that cycles through a few sample objects so the app is runnable
 * without camera/ML hardware. Replace with a camera-backed recognizer for production.
 */
class DemoObjectRecognizer : ObjectRecognizer {
    private val samples = listOf(
        RecognizedObject("nike-af1", "Nike Air Force 1", 0.94f),
        RecognizedObject("psa-charizard", "Charizard Holo · PSA 9", 0.88f),
        RecognizedObject("kitchenaid", "KitchenAid Stand Mixer", 0.91f),
        RecognizedObject("eames-chair", "Eames Lounge Chair", 0.83f),
    )

    override fun recognitions(): Flow<RecognizedObject?> = flow {
        var index = 0
        while (true) {
            emit(null)                 // "searching" gap between objects
            delay(1_200)
            emit(samples[index % samples.size])
            delay(4_000)               // dwell on the identified object
            index++
        }
    }
}
