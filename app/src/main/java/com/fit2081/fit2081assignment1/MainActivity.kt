/**
 * MainActivity.kt
 *
 * This file serves as the entry point for the NutriTrack app. It sets up the app's theme,
 * initializes the navigation controller, and creates a navigation host (AppNavHost) that
 * defines the navigation routes for the app. The app includes screens for Welcome, Sign In,
 * a Food Questionnaire, Home, and Insights.
 */
package com.fit2081.fit2081assignment1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fit2081.fit2081assignment1.data.AppDatabase
import com.fit2081.fit2081assignment1.repository.NutriTrackRepository
import com.fit2081.fit2081assignment1.ui.theme.FIT2081Assignment1Theme

class MainActivity : ComponentActivity() {
    // Initialize the FoodQuestionnaireViewModel for sharing state across composables.
    private lateinit var questionnaireViewModel: FoodQuestionnaireViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display mode for a modern UI look.
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)

        DatabaseSeeder.seedDatabaseIfNeeded(this, db)

        val repository = NutriTrackRepository(db)
        val factory = FoodQuestionnaireViewModelFactory(repository)

        questionnaireViewModel = ViewModelProvider(this, factory)[FoodQuestionnaireViewModel::class.java]

        setContent {
            // Apply the custom theme for the app.
            FIT2081Assignment1Theme {
                // Create a navigation controller to manage navigation between screens.
                val navController = rememberNavController()
                // Set up the navigation host with all the routes and pass the shared ViewModel.
                AppNavHost(navController = navController, viewModel = questionnaireViewModel)
            }
        }
    }
}


/**
 * AppNavHost sets up the navigation graph for the app.
 * It defines the start destination and the routes for each composable screen.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: FoodQuestionnaireViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        // Define route for WelcomeScreen.
        composable("welcome") { WelcomeScreen(navController) }
        // Define route for SignInScreen.
        composable("signin") { SignInScreen(navController) }
        // Define route for Registration.
        composable("register") { RegisterScreen(navController) }
        // Define route for FoodQuestionnaireScreen.
        composable("food_questionnaire") { FoodQuestionnaireScreen(navController, viewModel) }
        // Define route for HomeScreen.
        composable("home") { HomeScreen(navController) }
        // Define route for InsightsScreen.
        composable("insights") { InsightsScreen(navController, viewModel) }
        // Define route for Nutrition Coach.
        composable("nutricoach") { NutriCoachScreen(navController) }
        // Define route for Settings.
        composable("settings") { SettingsScreen(navController, viewModel) }
        composable("clinician_login") { ClinicianLoginScreen(navController) }
    }
}

/**
 * WelcomeScreen displays a welcome message, app information, and a login button.
 * It also provides a clickable link to an external nutrition clinic website.
 */
@Composable
fun WelcomeScreen(navController: NavController) {
    // Retrieve the current context for launching intents.
    val context = LocalContext.current

    // Main container: vertically centered Column with padding.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the app logo.
        Image(
            painter = painterResource(R.mipmap.ic_launcher),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display the welcome title.
        Text(
            text = "Welcome to NutriTrack",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display a brief description and disclaimer text.
        Text(
            text = "This app provides general health and nutrition information for educational purposes only. " +
                    "It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified " +
                    "healthcare professional before making any changes to your diet, exercise, or health regimen.\n\n" +
                    "Use this app at your own risk.\n\n" +
                    "If you’d like to see an Accredited Practicing Dietitian (APD), visit:",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display a clickable text link to the Monash Nutrition Clinic.
        ClickableText(
            text = AnnotatedString("Monash Nutrition Clinic"),
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
            onClick = {
                // Launch an external browser to open the URL.
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button navigates to the SignInScreen.
        Button(onClick = {
            Log.d("NAVIGATION", "Navigating to SignInScreen")
            navController.navigate("signin")
        }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Student Name + ID Footer
        Text(
            text = "FIT2081 student project",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
