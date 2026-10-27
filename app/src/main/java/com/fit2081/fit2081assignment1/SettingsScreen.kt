package com.fit2081.fit2081assignment1

/**
 * SettingsScreen.kt
 *
 * This composable displays the Settings screen of the NutriTrack app.
 * It includes user information (username, phone number, and user ID),
 * and provides options to logout or log in as a clinician.
 * The screen includes a bottom navigation bar for easy access to
 * other sections of the app.
 */

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fit2081.fit2081assignment1.ui.theme.FIT2081Assignment1Theme

@Composable
fun SettingsScreen(navController: NavController, viewModel: FoodQuestionnaireViewModel) {
    // Access the current context for session handling and navigation
    val context = LocalContext.current

    // Retrieve session information and fallback if data is not available
    val userId = getSessionUserId(context) ?: "Unknown User"
    val phoneNumber = getSessionPhoneNumber(context).takeUnless { it.isNullOrBlank() } ?: "Phone number not available"
    val userName = "User $userId"

    // Create a scaffold layout with bottom navigation
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Define navigation to Home screen
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate("home") }
                )
                // Define navigation to Insights screen
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Insights") },
                    label = { Text("Insights") },
                    selected = false,
                    onClick = { navController.navigate("insights") }
                )
                // Define navigation to NutriCoach screen
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "NutriCoach") },
                    label = { Text("NutriCoach") },
                    selected = false,
                    onClick = { navController.navigate("nutricoach") }
                )
                // Define navigation to Settings screen
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { innerPadding ->
        // Layout for content inside the Settings screen
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // --- Account Info ---
            Text("ACCOUNT", color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Display name with an icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Name")
                Spacer(modifier = Modifier.width(8.dp))
                Text(userName)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display phone with an icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = "Phone")
                Spacer(modifier = Modifier.width(8.dp))
                Text(phoneNumber)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display userId with an icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Badge, contentDescription = "User ID")
                Spacer(modifier = Modifier.width(8.dp))
                Text(userId)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- Other Settings ---
            Text("OTHER SETTINGS", color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Logout button clears session and navigates to Sign In screen
            Button(
                onClick = {
                    clearSession(context)
                    navController.navigate("signin") {
                        popUpTo("signin") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button to navigate to the Clinician Login screen
            Button(
                onClick = {
                    navController.navigate("clinician_login")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = "Clinician Login")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clinician Login")
            }
        }
    }
}