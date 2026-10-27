/**
 * UserData.kt
 *
 * This data class represents a single row of user data extracted from the CSV file ("UserDetail.csv").
 * It stores all relevant information about the user including their identification details, sex,
 * and various food quality scores. The scores include a total score (separately calculated for
 * males and females) as well as detailed scores for individual food/nutrient categories.
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

data class UserData(
    val userId: String,
    val phoneNumber: String,
    val sex: String,
    val totalScoreMale: String,
    val totalScoreFemale: String,
    val discretionaryScore: String,
    val vegetableScore: String,
    val fruitScore: String,
    val grainScore: String,
    val meatScore: String,
    val dairyScore: String,
    val waterScore: String,
    val fatsScore: String,
    val sodiumScore: String,
    val addedSugarsScore: String,
    val alcoholScore: String
)