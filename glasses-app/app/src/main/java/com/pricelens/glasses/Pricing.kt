package com.pricelens.glasses

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Resolves an estimated market price for a recognised object.
 *
 * Keep it off the main thread — it is a `suspend` function. Implementations should be
 * resilient (timeouts, graceful "unavailable") since they run on every new identification.
 */
interface PricingService {
    suspend fun priceFor(item: RecognizedObject): PriceState
}

/**
 * Concrete pricing via SerpApi's Google Shopping engine.
 *
 * Provide an API key (see README — set `serpApiKey` as a Gradle property and it is wired
 * to `BuildConfig.SERPAPI_KEY`). To use a different marketplace (eBay, StockX, …) swap the
 * endpoint and the JSON field extraction; the rest of the app is unaffected.
 */
class SerpApiPricingService(
    private val apiKey: String,
) : PricingService {

    override suspend fun priceFor(item: RecognizedObject): PriceState = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext PriceState.Unavailable("Set serpApiKey to enable pricing")
        }
        try {
            val query = URLEncoder.encode(item.label, "UTF-8")
            val endpoint = URL(
                "https://serpapi.com/search.json?engine=google_shopping&q=$query&api_key=$apiKey",
            )
            val connection = (endpoint.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8_000
                readTimeout = 8_000
            }
            connection.inputStream.bufferedReader().use { reader ->
                val first = JSONObject(reader.readText())
                    .optJSONArray("shopping_results")
                    ?.optJSONObject(0)
                if (first == null) {
                    PriceState.Unavailable("No price found")
                } else {
                    val price = first.optString("price").ifBlank {
                        val extracted = first.optDouble("extracted_price", Double.NaN)
                        if (extracted.isNaN()) "" else "\$$extracted"
                    }
                    val source = first.optString("source").ifBlank { "Google Shopping" }
                    if (price.isBlank()) {
                        PriceState.Unavailable("No price found")
                    } else {
                        PriceState.Available(price, source)
                    }
                }
            }
        } catch (e: Exception) {
            PriceState.Unavailable(e.message ?: "Lookup failed")
        }
    }
}

/**
 * Wraps a [PricingService] with a short-lived in-memory cache keyed by object id.
 *
 * Only successful ([PriceState.Available]) results are cached, so a transient failure or
 * a "no price found" is retried next time rather than being stuck for the whole TTL.
 * Access is guarded by a [Mutex] since lookups run concurrently from the scan pipeline.
 */
class CachingPricingService(
    private val delegate: PricingService,
    private val ttlMillis: Long = TimeUnit.MINUTES.toMillis(5),
    private val now: () -> Long = System::currentTimeMillis,
) : PricingService {

    private data class Entry(val price: PriceState, val expiresAt: Long)

    private val mutex = Mutex()
    private val cache = mutableMapOf<String, Entry>()

    override suspend fun priceFor(item: RecognizedObject): PriceState {
        val key = item.id

        mutex.withLock {
            val cached = cache[key]
            when {
                cached == null -> Unit
                cached.expiresAt > now() -> return cached.price
                else -> cache.remove(key) // expired
            }
        }

        val price = delegate.priceFor(item)
        if (price is PriceState.Available) {
            mutex.withLock { cache[key] = Entry(price, now() + ttlMillis) }
        }
        return price
    }
}

/** Demo pricing with canned values so the UI shows realistic numbers offline. */
class DemoPricingService : PricingService {
    private val catalog = mapOf(
        "nike-air-force-1" to ("$110" to "Retail"),
        "shoe" to ("$110" to "Retail"),
        "furniture" to ("$5,495" to "Herman Miller"),
        "kitchen-appliance" to ("$379" to "MSRP"),
    )

    override suspend fun priceFor(item: RecognizedObject): PriceState {
        delay(600) // simulate a network round-trip
        val match = catalog[item.id] ?: return PriceState.Unavailable("No price found")
        return PriceState.Available(formattedPrice = match.first, source = match.second)
    }
}
