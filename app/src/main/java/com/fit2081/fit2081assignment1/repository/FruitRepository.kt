// FruitRepository.kt
// This class serves as a repository layer for fetching fruit nutrition data.
// It provides an abstraction over the API access code using Retrofit.

package com.fit2081.fit2081assignment1.repository

import com.fit2081.fit2081assignment1.data.api.FruitResponse
import com.fit2081.fit2081assignment1.data.api.RetrofitInstance

// Repository class responsible for fetching fruit nutritional data.
class FruitRepository {

    // This suspend function fetches the nutritional information of a fruit
    // by delegating the network call to the Retrofit API instance.
    suspend fun fetchFruitNutrition(fruitName: String): FruitResponse {
        // Calls the API method to get fruit data by name.
        return RetrofitInstance.api.getFruitByName(fruitName)
    }
}