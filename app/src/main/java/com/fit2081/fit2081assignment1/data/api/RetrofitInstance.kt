// RetrofitInstance.kt
// This singleton object provides a lazily initialized Retrofit instance used for accessing
// the FruityVice API. It uses Retrofit with a Gson converter to handle HTTP requests and JSON responses.

package com.fit2081.fit2081assignment1.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Lazily initialized API service for FruityVice API.
    val api: FruityViceApi by lazy {
        Retrofit.Builder()
            // Base URL of the FruityVice API. The trailing slash is required for proper endpoint resolution.
            .baseUrl("https://www.fruityvice.com/")
            // Use Gson converter to automatically parse JSON responses into Kotlin objects.
            .addConverterFactory(GsonConverterFactory.create())
            // Build the Retrofit instance and create an implementation of the API interface.
            .build()
            .create(FruityViceApi::class.java)
    }
}