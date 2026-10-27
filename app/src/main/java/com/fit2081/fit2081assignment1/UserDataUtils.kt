/**
 * UserDataUtils.kt
 *
 * This file contains utility functions to read user data from a CSV file and validate the user input.
 * It parses the CSV (UserDetail.csv) into a list of UserData objects (containing details like userId,
 * phoneNumber, sex, total scores, and detailed scores for various food categories) and then validates
 * user credentials. When a match is found, it updates the shared FoodQuestionnaireViewModel with the
 * corresponding data (including detailed scores stored as a map) so that other screens can display the data.
 */
package com.fit2081.fit2081assignment1

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
import android.content.Context
import java.io.IOException

fun readUserDataFromCSV(context: Context): List<UserData> {
    // Create a mutable list to hold the parsed UserData objects.
    val userList = mutableListOf<UserData>()
    try {
        // Open the CSV file from the assets folder and read its lines.
        context.assets.open("UserDetail.csv").bufferedReader().useLines { lines ->
            // Skip the header row and iterate through each line.
            lines.drop(1).forEach { line ->
                // Split each line by commas.
                val tokens = line.split(",")
                // Check that there are at least 16 tokens (columns) in the line.
                if (tokens.size >= 16) {
                    // Trim and assign each token to its corresponding variable.
                    val phoneNumber = tokens[0].trim()
                    val userId = tokens[1].trim()
                    val sex = tokens[2].trim()
                    val totalScoreMale = tokens[3].trim()
                    val totalScoreFemale = tokens[4].trim()
                    val discretionaryScore = tokens[5].trim()
                    val vegetableScore = tokens[6].trim()
                    val fruitScore = tokens[7].trim()
                    val grainScore = tokens[8].trim()
                    val meatScore = tokens[9].trim()
                    val dairyScore = tokens[10].trim()
                    val waterScore = tokens[11].trim()
                    val fatsScore = tokens[12].trim()
                    val sodiumScore = tokens[13].trim()
                    val addedSugarsScore = tokens[14].trim()
                    val alcoholScore = tokens[15].trim()
                    // Create a UserData object with the parsed values and add it to the list.
                    userList.add(
                        UserData(
                            userId,
                            phoneNumber,
                            sex,
                            totalScoreMale,
                            totalScoreFemale,
                            discretionaryScore,
                            vegetableScore,
                            fruitScore,
                            grainScore,
                            meatScore,
                            dairyScore,
                            waterScore,
                            fatsScore,
                            sodiumScore,
                            addedSugarsScore,
                            alcoholScore
                        )
                    )
                }
            }
        }
    } catch (e: IOException) {
        // Print the stack trace if an error occurs while reading the file.
        e.printStackTrace()
    }
    // Return the list of parsed UserData objects.
    return userList
}

/**
 * validateUserInput validates the given userIdInput and phoneNumberInput by reading the CSV file.
 * If a matching user is found, it updates the FoodQuestionnaireViewModel with user details and scores.
 *
 * @param userIdInput The user ID entered by the user.
 * @param phoneNumberInput The phone number entered by the user.
 * @param context The context used to access assets.
 * @param viewModel The shared FoodQuestionnaireViewModel to update with the user's data.
 * @return True if a matching user is found and the ViewModel is updated; false otherwise.
 */
fun validateUserInput(
    userIdInput: String,
    phoneNumberInput: String,
    context: Context,
    viewModel: FoodQuestionnaireViewModel
): Boolean {
    // Read the list of user data from the CSV.
    val users = readUserDataFromCSV(context)
    // Find the first user whose userId and phoneNumber match the provided input.
    val matchedUser = users.firstOrNull { it.userId == userIdInput && it.phoneNumber == phoneNumberInput }
    return if (matchedUser != null) {
        // If a matching user is found, update the ViewModel with user details.
        viewModel.userId.value = matchedUser.userId
        viewModel.phoneNumber.value = matchedUser.phoneNumber
        viewModel.sex.value = matchedUser.sex
        // Save the total scores for both males and females in the ViewModel.
        viewModel.userScoreMale.value = matchedUser.totalScoreMale
        viewModel.userScoreFemale.value = matchedUser.totalScoreFemale

        // Save detailed scores in the ViewModel as a map, where keys are category names.
        viewModel.detailedScores.value = mapOf(
            "Discretionary foods" to matchedUser.discretionaryScore,
            "Vegetables" to matchedUser.vegetableScore,
            "Fruits" to matchedUser.fruitScore,
            "Grains and Cereals" to matchedUser.grainScore,
            "Meat and Alternatives" to matchedUser.meatScore,
            "Dairy and Alternatives" to matchedUser.dairyScore,
            "Water" to matchedUser.waterScore,
            "Fats" to matchedUser.fatsScore,
            "Sodium" to matchedUser.sodiumScore,
            "Added sugars" to matchedUser.addedSugarsScore,
            "Alcohol" to matchedUser.alcoholScore
        )
        // Set the displayed total score based on the user's sex.
        viewModel.userScore.value = if (matchedUser.sex.equals("Female", ignoreCase = true)) {
            matchedUser.totalScoreFemale
        } else {
            matchedUser.totalScoreMale
        }
        // Return true indicating validation success.
        true
    } else {
        // Return false if no matching user is found.
        false
    }
}
