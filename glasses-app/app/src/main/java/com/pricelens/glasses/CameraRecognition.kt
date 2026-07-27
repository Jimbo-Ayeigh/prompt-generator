package com.pricelens.glasses

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.concurrent.futures.await
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.Executors

/**
 * Real recognizer: runs ML Kit on-device image labeling over CameraX frames.
 *
 * Note on granularity: the default ML Kit labeler returns *generic* labels ("Shoe",
 * "Furniture") with a confidence score — good enough for category-level pricing. For
 * fine-grained product identity ("Nike Air Force 1") swap in a custom-trained model or a
 * product-search backend; the rest of the pipeline is unaffected.
 */
class MlKitObjectRecognizer : ObjectRecognizer, ImageAnalysis.Analyzer {

    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private val recognized = MutableStateFlow<RecognizedObject?>(null)

    override fun recognitions(): Flow<RecognizedObject?> = recognized

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val input = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        labeler.process(input)
            .addOnSuccessListener { labels ->
                val best = labels
                    .maxByOrNull { it.confidence }
                    ?.takeIf { it.confidence >= MIN_CONFIDENCE }
                val next = best?.let {
                    RecognizedObject(id = slug(it.text), label = it.text, confidence = it.confidence)
                }
                // Only publish when the identified object actually changes, so the UI
                // (and the downstream price lookup) don't churn on every frame.
                if (next?.id != recognized.value?.id) {
                    recognized.value = next
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun slug(text: String) = text.lowercase().trim().replace(Regex("\\s+"), "-")

    companion object {
        private const val MIN_CONFIDENCE = 0.6f
    }
}

/**
 * Sets up CameraX image analysis bound to the current lifecycle and feeds frames into a
 * [MlKitObjectRecognizer]. Call this only after the CAMERA permission has been granted.
 */
@Composable
fun rememberCameraRecognizer(): ObjectRecognizer {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val recognizer = remember { MlKitObjectRecognizer() }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(lifecycleOwner) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).await()
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { setAnalyzer(analysisExecutor, recognizer) }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            analysis,
        )
    }
    return recognizer
}
