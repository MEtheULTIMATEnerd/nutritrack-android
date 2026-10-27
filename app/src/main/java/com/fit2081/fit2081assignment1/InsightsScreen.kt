/**
 * InsightsScreen.kt
 *
 * This composable function displays the Insights Screen for the NutriTrack app.
 * It provides a detailed breakdown of the user's food score by showing progress bars for each food category.
 * The detailed scores are extracted from the CSV and stored in the ViewModel.
 * The screen also displays the total Food Quality Score and includes two buttons:
 *   - "Share with someone" for sharing the score via an intent.
 *   - "Improve my diet" for navigation to the NutriCoach screen.
 * A bottom navigation bar allows navigation between Home, Insights, NutriCoach, and Settings.
 */
package com.fit2081.fit2081assignment1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fit2081.fit2081assignment1.ui.theme.FIT2081Assignment1Theme

@Composable
fun InsightsScreen(navController: NavController, viewModel: FoodQuestionnaireViewModel) {
    // Fetch user data from the ViewModel
    val patient = viewModel.patientData.value

    // Decide which total score to show based on the user's sex
    val totalScore = if (patient?.sex.equals("Female", ignoreCase = true)) {
        patient?.totalScoreFemale ?: "0"
    } else {
        patient?.totalScoreMale ?: "0"
    }

    // Map each food category to its score for detailed breakdown
    val detailedScores = mapOf(
        "Discretionary Foods" to (patient?.discretionaryScore ?: "0"),
        "Vegetables" to (patient?.vegetableScore ?: "0"),
        "Fruits" to (patient?.fruitScore ?: "0"),
        "Grains & Cereals" to (patient?.grainScore ?: "0"),
        "Whole Grains" to "0",  // Placeholder if not available
        "Meat & Alternatives" to (patient?.meatScore ?: "0"),
        "Dairy" to (patient?.dairyScore ?: "0"),
        "Water" to (patient?.waterScore ?: "0"),
        "Unsaturated Fats" to (patient?.fatsScore ?: "0"),
        "Sodium" to (patient?.sodiumScore ?: "0"),
        "Sugar" to (patient?.addedSugarsScore ?: "0"),
        "Alcohol" to (patient?.alcoholScore ?: "0")
    )

    // Get current context and user ID for loading data
    val context = LocalContext.current
    val userId = getSessionUserId(context) ?: "Guest"

    // Load patient details when screen is launched
    LaunchedEffect(Unit) {
        viewModel.loadPatient(userId)
    }

    // Define the maximum possible score for each food category
    val maxScores = mapOf(
        "Discretionary Foods" to 10f,
        "Vegetables" to 10f,
        "Fruits" to 10f,
        "Grains & Cereals" to 10f,
        "Whole Grains" to 10f,
        "Meat & Alternatives" to 10f,
        "Dairy" to 10f,
        "Water" to 5f,
        "Unsaturated Fats" to 10f,
        "Sodium" to 10f,
        "Sugar" to 10f,
        "Alcohol" to 5f
    )

    // Scroll state to make content vertically scrollable
    val scrollState = rememberScrollState()

    // Scaffold with bottom navigation bar
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate("home") })
                NavigationBarItem(icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
                    label = { Text("Insights") },
                    selected = true,
                    onClick = { })  // Already on this screen
                NavigationBarItem(icon = { Icon(Icons.Default.Favorite, contentDescription = "NutriCoach") },
                    label = { Text("NutriCoach") },
                    selected = false,
                    onClick = { navController.navigate("nutricoach") })
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { navController.navigate("settings") })
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(end = 12.dp, start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                // Title
                Text(
                    text = "Insights: Food Score",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Progress bars for each food category
                maxScores.forEach { (category, maxScore) ->
                    val score = detailedScores[category]?.toFloatOrNull() ?: 0f
                    val progress = score / maxScore

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = category, modifier = Modifier.weight(1f))
                        LinearProgressIndicator(progress = progress, modifier = Modifier.weight(2f))
                        Text(
                            text = String.format("%.2f / %.0f", score, maxScore),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display Total Food Quality Score
                Text("Total Food Quality Score", style = MaterialTheme.typography.titleMedium)
                val totalScoreVal = totalScore.toFloatOrNull() ?: 0f
                val totalProgress = totalScoreVal / 100f

                LinearProgressIndicator(
                    progress = totalProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Text(
                    text = String.format("%.2f/100", totalScoreVal),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Share button to send score via intent
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Check out my Food Quality Score!")
                            putExtra(Intent.EXTRA_TEXT, "My Food Quality Score is $totalScore!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share with someone")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigate to NutriCoach screen
                Button(
                    onClick = {
                        navController.navigate("nutricoach")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Improve my diet!")
                }
            }
        }
    }
}