// NutriCoachScreen.kt
//
// This composable screen is part of the NutriTrack app. It allows users to:
// - Enter a fruit name and retrieve its nutritional information from the FruityVice API.
// - Display that information in visually separated cards.
// - Request a motivational health message about the fruit using Gemini AI.
// - View a list of all previously generated tips in a popup dialog.
// The screen includes a bottom navigation bar linking to Home, Insights, NutriCoach, and Settings.

package com.fit2081.fit2081assignment1

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fit2081.fit2081assignment1.data.api.FruitResponse
import com.fit2081.fit2081assignment1.data.api.GeminiService
import com.fit2081.fit2081assignment1.data.api.RetrofitInstance
import com.fit2081.fit2081assignment1.repository.FruitRepository
import kotlinx.coroutines.launch

// Reusable UI component to display a labeled nutrient value (e.g., "Calories : 56") in a styled Card.
@Composable
fun NutrientCardRow(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Adds vertical spacing between cards
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = ":", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Main composable function for the NutriCoach screen.
// Users can input a fruit, retrieve nutrition info, request a motivational tip, and view all tips in a dialog.
@Composable
fun NutriCoachScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fruitRepository = FruitRepository()

    // State variables for user input and data display
    var fruitName by remember { mutableStateOf("") }                    // Input for the fruit name
    var fruitData by remember { mutableStateOf<FruitResponse?>(null) } // Stores response from FruityVice API
    var motivationMessage by remember { mutableStateOf("") }           // Stores motivational message
    var isLoading by remember { mutableStateOf(false) }                // Used to show loading state
    val tipList = remember { mutableStateListOf<String>() }            // Stores list of all tips
    var showDialog by remember { mutableStateOf(false) }               // Flag to show tip list dialog

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            // Bottom navigation bar to switch between app screens
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, label = { Text("Home") }, selected = false, onClick = { navController.navigate("home") })
                NavigationBarItem(icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") }, label = { Text("Insights") }, selected = false, onClick = { navController.navigate("insights") })
                NavigationBarItem(icon = { Icon(Icons.Default.Favorite, contentDescription = "NutriCoach") }, label = { Text("NutriCoach") }, selected = true, onClick = { })
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }, label = { Text("Settings") }, selected = false, onClick = { navController.navigate("settings") })
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("NutriCoach", style = MaterialTheme.typography.headlineSmall)

            // Fruit name input field
            OutlinedTextField(
                value = fruitName,
                onValueChange = { fruitName = it },
                label = { Text("Fruit Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Button to fetch fruit nutrition data from API
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            fruitData = fruitRepository.fetchFruitNutrition(fruitName.lowercase())
                        } catch (e: Exception) {
                            fruitData = null
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Text("Details")
            }

            // Display the fruit data if available
            fruitData?.let { fruit ->
                NutrientCardRow("family", fruit.family)
                NutrientCardRow("calories", fruit.nutritions.calories.toString())
                NutrientCardRow("fat", fruit.nutritions.fat.toString())
                NutrientCardRow("sugar", fruit.nutritions.sugar.toString())
                NutrientCardRow("carbohydrates", fruit.nutritions.carbohydrates.toString())
                NutrientCardRow("protein", fruit.nutritions.protein.toString())
            }

            Divider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // Button to request motivational message using Gemini
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        motivationMessage = GeminiService.getMotivationalMessage(
                            "Give me a short, positive and specific health tip related to eating $fruitName. Keep it under 25 words, friendly tone, and highlight 1 health benefit."
                        )
                        if (motivationMessage.isNotBlank()) {
                            tipList.add(motivationMessage)
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Message, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Motivational Message (AI)")
            }

            // Display the latest motivational message
            if (motivationMessage.isNotBlank()) {
                Text(
                    text = motivationMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Button to show all motivational tips in a dialog
            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Shows All Tips")
            }

            // Dialog to display all saved motivational tips
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("All Motivational Tips") },
                    text = {
                        Column {
                            tipList.forEach { tip ->
                                Text(text = tip, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}