// GeminiService.kt
// This object handles API interaction with Google Gemini's generative language model.
// It constructs a JSON prompt, sends a POST request with an API key to get a motivational message about a fruit,
// and parses the response to extract the tip for display in the app.

package com.fit2081.fit2081assignment1.data.api

import com.fit2081.fit2081assignment1.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.*
import kotlinx.serialization.json.*

object GeminiService {

    // Base URL endpoint for Gemini's content generation.
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro-001:generateContent"

    // Reusable Ktor HTTP client with JSON serialization.
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // Ignore unknown fields in the response to prevent deserialization errors.
        }
    }

    // Asynchronous function to fetch a motivational message about the given fruit.
    suspend fun getMotivationalMessage(fruitName: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isEmpty()) {
            return "Gemini is disabled for this build. Add GEMINI_API_KEY only for local testing."
        }
        // Build the JSON request payload with the prompt text.
        val requestBody = buildJsonObject {
            putJsonArray("contents") {
                addJsonObject {
                    putJsonArray("parts") {
                        addJsonObject {
                            put(
                                "text",
                                "You are a health and wellness assistant. Give one short, friendly, motivational health tip about eating $fruitName. " +
                                        "Keep it natural, energetic, and inspiring. Add one health benefit of the fruit."
                            )
                        }
                    }
                }
            }
        }

        return try {
            // Send POST request to Gemini API with the prompt.
            val response: HttpResponse = client.post("$BASE_URL?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            // Log response status and raw body (for debugging).
            val responseText = response.bodyAsText()

            // Parse the JSON response and extract the motivational text.
            val jsonElement = Json.parseToJsonElement(responseText)
            val candidates = jsonElement.jsonObject["candidates"]?.jsonArray
            val content = candidates?.getOrNull(0)?.jsonObject?.get("content")?.jsonObject
            val parts = content?.get("parts")?.jsonArray
            val text = parts?.getOrNull(0)?.jsonObject?.get("text")?.jsonPrimitive?.content

            // Return the extracted motivational text, or fallback if null.
            text ?: "No motivational tip received from Gemini."

        } catch (e: Exception) {
            // Handle and log any exceptions during the API request or parsing.
            "Gemini unavailable. Try again later or use the offline features."
        }
    }
}
