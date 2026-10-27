/**
 * HomeScreen.kt
 *
 * This composable displays the Home Screen for the NutriTrack app. It shows:
 * - A greeting using the user ID (since no name is provided).
 * - A score breakdown image (converted from score.pdf and placed in drawable).
 * - A toggle button ("See all scores") that shows or hides a detailed breakdown of individual scores
 *   extracted from the CSV (detailedScores map).
 * - The total Food Quality Score loaded from the CSV.
 * - An explanation of the Food Quality Score.
 * Additionally, a bottom navigation bar is provided to navigate between Home, Insights, NutriCoach, and Settings.
 */
package com.fit2081.fit2081assignment1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.fit2081.fit2081assignment1.data.AppDatabase
import com.fit2081.fit2081assignment1.ui.theme.FIT2081Assignment1Theme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun HomeScreen(navController: NavController) {
    // Access context to retrieve user session
    val context = LocalContext.current
    val userId = getSessionUserId(context) ?: "Guest"  // Default to "Guest" if no userId found

    // State holders for scores
    var totalScore by remember { mutableStateOf("N/A") }
    var detailedScores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Load user dietary data from the Room database on launch
    LaunchedEffect(Unit) {
        val dao = AppDatabase.getDatabase(context).patientDao()
        val patient = dao.getPatientById(userId)

        patient?.let {
            totalScore = if (it.sex.equals("Female", ignoreCase = true)) {
                it.totalScoreFemale ?: "N/A"
            } else {
                it.totalScoreMale ?: "N/A"
            }

            // Populate detailed score map
            detailedScores = mapOf(
                "Discretionary foods"   to (it.discretionaryScore ?: "0"),
                "Vegetables"            to (it.vegetableScore ?: "0"),
                "Fruits"                to (it.fruitScore ?: "0"),
                "Grains and Cereals"    to (it.grainScore ?: "0"),
                "Meat and Alternatives" to (it.meatScore ?: "0"),
                "Dairy and Alternatives" to (it.dairyScore ?: "0"),
                "Water"                 to (it.waterScore ?: "0"),
                "Fats"                  to (it.fatsScore ?: "0"),
                "Sodium"                to (it.sodiumScore ?: "0"),
                "Added sugars"          to (it.addedSugarsScore ?: "0"),
                "Alcohol"               to (it.alcoholScore ?: "0")
            )
        }
    }

    var showDetailedScores by remember { mutableStateOf(false) }  // State to toggle detailed score visibility
    val currentRoute = "home"

    Scaffold(
        bottomBar = {
            // Bottom navigation bar with 4 screens
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { }  // Already on Home
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
                    label = { Text("Insights") },
                    selected = currentRoute == "insights",
                    onClick = { navController.navigate("insights") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "NutriCoach") },
                    label = { Text("NutriCoach") },
                    selected = currentRoute == "nutricoach",
                    onClick = { navController.navigate("nutricoach") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentRoute == "settings",
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    ) { innerPadding ->
        // Scrollable content using LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Top-right edit button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = { navController.navigate("food_questionnaire") }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Text("Edit", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            item {
                // Greeting text
                Text(
                    text = "Hello, $userId",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                // Image showing score breakdown
                Image(
                    painter = painterResource(id = R.drawable.score),
                    contentDescription = "Score Breakdown Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            item {
                // Toggle button to show/hide detailed scores
                Button(
                    onClick = { showDetailedScores = !showDetailedScores },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(if (showDetailedScores) "Hide scores" else "See all scores")
                }
            }

            // Conditional rendering of score breakdown
            if (showDetailedScores) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        detailedScores.forEach { (category, score) ->
                            Text(text = "$category: $score", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Text(
                    text = "Your Food Quality Score:",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Text(
                    text = totalScore,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                // Description of what the Food Quality Score means
                Text(
                    text = "The Food Quality Score is based on your dietary intake and is calculated using guidelines specific to your sex. Higher scores reflect a diet that better meets nutritional recommendations.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}