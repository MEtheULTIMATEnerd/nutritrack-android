/**
 * SessionManager.kt
 *
 * This file contains utility functions for managing user session data
 * using Android's SharedPreferences.
 * The session is used to remember the logged-in user's ID and optionally
 * the phone number between app launches.
 */

package com.fit2081.fit2081assignment1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fit2081.fit2081assignment1.ui.theme.FIT2081Assignment1Theme

/**
 * Saves the currently logged-in user's ID to SharedPreferences.
 * This method enables persistence of login across app launches.
 *
 * @param context The application or activity context.
 * @param userId The user ID to store.
 */
fun saveSession(context: Context, userId: String) {
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    prefs.edit().putString("loggedInUserId", userId).apply()
}

/**
 * Retrieves the currently saved user ID from SharedPreferences.
 * Returns null if no session is stored.
 *
 * @param context The application or activity context.
 * @return The stored user ID or null if not available.
 */
fun getSessionUserId(context: Context): String? {
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    return prefs.getString("loggedInUserId", null)
}

/**
 * Retrieves the stored phone number associated with the session.
 * Note: This assumes the phone number was previously saved in prefs.
 *
 * @param context The application or activity context.
 * @return The stored phone number or null if not available.
 */
fun getSessionPhoneNumber(context: Context): String? {
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    return prefs.getString("loggedInPhoneNumber", null)
}

/**
 * Clears the current user session by removing the saved user ID.
 * Typically used during logout.
 *
 * @param context The application or activity context.
 */
fun clearSession(context: Context) {
    val prefs = context.getSharedPreferences("NutriTrackPrefs", Context.MODE_PRIVATE)
    prefs.edit().remove("loggedInUserId").apply()
}