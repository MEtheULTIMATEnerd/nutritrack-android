// FruityViceApi.kt
// This file handles API communication with the FruityVice API service.
// It defines a Retrofit interface for fetching fruit nutrition data based on fruit name,
// and a function using Ktor HTTP client to fetch and format fruit nutrition information as a string output.

package com.fit2081.fit2081assignment1.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Retrofit interface for FruityVice API.
// This function sends a GET request to /api/fruit/{name} and expects a FruitResponse.
interface FruityViceApi {
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): FruitResponse
}

// Ktor-based function to fetch fruit nutrition details and return a formatted string.
suspend fun fetchFruitNutrition(fruitName: String): String {
    // Create an HTTP client with CIO engine and JSON content negotiation.
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // Allows ignoring extra fields in JSON response.
        }
    }

    return try {
        // Send a GET request to the FruityVice API with the given fruit name (converted to lowercase).
        val response: FruitResponse = client.get("https://www.fruityvice.com/api/fruit/${fruitName.lowercase()}").body()
        val nutrition = response.nutritions

        // Format the API response into a user-friendly string.
        buildString {
            appendLine("Fruit: ${response.name}")
            appendLine("Family: ${response.family}")
            appendLine("Genus: ${response.genus}")
            appendLine("Order: ${response.order}")
            appendLine("Nutrition:")
            appendLine("- Carbohydrates: ${nutrition.carbohydrates}g")
            appendLine("- Protein: ${nutrition.protein}g")
            appendLine("- Fat: ${nutrition.fat}g")
            appendLine("- Calories: ${nutrition.calories}")
            appendLine("- Sugar: ${nutrition.sugar}g")
        }
    } catch (e: Exception) {
        // If an error occurs (e.g. invalid name or network issue), return an error message.
        "Error fetching fruit data. Please check the name and try again."
    } finally {
        client.close() // Close the HTTP client to free up resources.
    }
}

// Data class representing the fruit response structure from the API.
@Serializable
data class FruitResponse(
    val name: String,
    val family: String,
    val genus: String,
    val order: String,
    val nutritions: Nutrition // Embedded object containing nutrition details.
)

// Data class representing nutritional values for a fruit.
@Serializable
data class Nutrition(
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val calories: Double,
    val sugar: Double
)