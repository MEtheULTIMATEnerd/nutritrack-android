// ClinicianLoginScreen.kt
// This composable defines the screen for clinician login with a bottom navigation bar.
// It accepts a single input field (clinician key) and navigates to the admin analytics screen if the key is valid.

package com.fit2081.fit2081assignment1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ClinicianLoginScreen(navController: NavController) {
    val context = LocalContext.current

    // State to hold the clinician key entered by the user.
    var clinicianKey by remember { mutableStateOf("") }

    // State to hold error messages shown on incorrect key.
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        // Bottom navigation bar with navigation items for all four main sections.
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate("home") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
                    label = { Text("Insights") },
                    selected = false,
                    onClick = { navController.navigate("insights") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "NutriCoach") },
                    label = { Text("NutriCoach") },
                    selected = false,
                    onClick = { navController.navigate("nutricoach") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = true,
                    onClick = { /* Already on Settings screen */ }
                )
            }
        }
    ) { paddingValues ->
        // Main content of the login screen, centered in the vertical space.
        Column(
            modifier = Modifier
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text field for clinician key input.
            OutlinedTextField(
                value = clinicianKey,
                onValueChange = { clinicianKey = it },
                label = { Text("Clinician Key") },
                modifier = Modifier.fillMaxWidth()
            )

            // Error message shown if login fails.
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button: checks the clinician key.
            Button(
                onClick = {
                    if (clinicianKey == "CLINICIAN123") {
                        // Navigate to admin analytics screen on successful login.
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        navController.navigate("admin_analytics")
                    } else {
                        // Show error message on failure.
                        errorMessage = "Invalid credentials. Try again."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Login, contentDescription = "Login")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login")
            }
        }
    }
}