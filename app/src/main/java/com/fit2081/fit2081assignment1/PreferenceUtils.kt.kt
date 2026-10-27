/**
 * QuestionnaireStorage.kt
 *
 * This file contains helper functions for persisting the user's food questionnaire selections locally
 * using Android's SharedPreferences. This includes saving and loading:
 *  - Selected food categories
 *  - Selected eating persona
 *  - Meal timing information (biggest meal, sleep time, wake time)
 *
 * These functions allow the app to maintain the user's progress across sessions.
 */

package com.fit2081.fit2081assignment1

import android.content.Context
import androidx.core.content.edit

// Name of the SharedPreferences file used to store questionnaire data
private const val PREFS_NAME = "QuestionnairePrefs"

// Keys used for each saved value
private const val KEY_CATEGORIES = "categories"
private const val KEY_PERSONA = "persona"
private const val KEY_MEAL_TIME = "meal_time"
private const val KEY_SLEEP_TIME = "sleep_time"
private const val KEY_WAKE_TIME = "wake_time"

/**
 * Saves the user's questionnaire selections into SharedPreferences.
 * This includes selected food categories, persona, and meal timing.
 *
 * @param context The Android context used to access preferences.
 * @param viewModel The ViewModel containing current user selections.
 */
fun saveQuestionnaireData(context: Context, viewModel: FoodQuestionnaireViewModel) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit {
        putStringSet(KEY_CATEGORIES, viewModel.selectedCategories.value) // Save selected food categories
        putString(KEY_PERSONA, viewModel.selectedPersona.value)          // Save selected persona
        putString(KEY_MEAL_TIME, viewModel.biggestMealTime.value)       // Save biggest meal time
        putString(KEY_SLEEP_TIME, viewModel.sleepTime.value)            // Save sleep time
        putString(KEY_WAKE_TIME, viewModel.wakeupTime.value)            // Save wake-up time
    }
}

/**
 * Loads the user's questionnaire selections from SharedPreferences into the ViewModel.
 * If no data exists, defaults are applied (empty sets or empty strings).
 *
 * @param context The Android context used to access preferences.
 * @param viewModel The ViewModel where loaded data will be stored.
 */
fun loadQuestionnaireData(context: Context, viewModel: FoodQuestionnaireViewModel) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    viewModel.selectedCategories.value = prefs.getStringSet(KEY_CATEGORIES, emptySet()) ?: emptySet()
    viewModel.selectedPersona.value = prefs.getString(KEY_PERSONA, "") ?: ""
    viewModel.biggestMealTime.value = prefs.getString(KEY_MEAL_TIME, "") ?: ""
    viewModel.sleepTime.value = prefs.getString(KEY_SLEEP_TIME, "") ?: ""
    viewModel.wakeupTime.value = prefs.getString(KEY_WAKE_TIME, "") ?: ""
}