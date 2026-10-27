/**
 * RegisterScreen.kt
 *
 * This composable screen allows pre-registered users to claim their account in the NutriTrack app.
 * Users select their pre-registered User ID, enter their phone number, and set a secure password.
 * The screen validates input, verifies user ID & phone number against the database,
 * and updates the stored password upon successful registration.
 *
 * Key Features:
 * - Dropdown for choosing existing User ID (from Room DB)
 * - Password fields with validation and visibility toggle
 * - Password strength feedback
 * - Registration confirmation and navigation to login screen
 */

package com.fit2081.fit2081assignment1

// Required imports for UI and coroutine/database interactions
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fit2081.fit2081assignment1.data.AppDatabase
import kotlinx.coroutines.*

/**
 * Helper function that checks password strength requirements:
 * - At least 10 characters
 * - Uppercase, lowercase, number, and special character
 * - No spaces allowed
 */
fun isPasswordValid(password: String): Boolean {
    val lengthRequirement = password.length >= 10
    val upperCaseRequirement = password.any { it.isUpperCase() }
    val lowerCaseRequirement = password.any { it.isLowerCase() }
    val digitRequirement = password.any { it.isDigit() }
    val specialCharRequirement = password.any { "!@#\$%^&*()-_=+[{]}|;:',<.>/?".contains(it) }
    val noSpaces = !password.contains(" ")
    return lengthRequirement && upperCaseRequirement && lowerCaseRequirement && digitRequirement && specialCharRequirement && noSpaces
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current

    // Load all pre-registered User IDs from Room
    val userIds = remember { mutableStateListOf<String>() }
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val ids = db.patientDao().getAllUserIds()
        userIds.addAll(ids)
    }

    // State variables for UI input
    var selectedUserId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Error and success messaging
    var generalError by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // Layout of registration form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown for selecting User ID
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedUserId,
                onValueChange = {},
                readOnly = true,
                label = { Text("My ID (Provided by your Clinician)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                userIds.forEach { userId ->
                    DropdownMenuItem(
                        text = { Text(userId) },
                        onClick = {
                            selectedUserId = userId
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input for phone number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input for password with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Show password guidance only if password is invalid
        if (password.isNotBlank() && !isPasswordValid(password)) {
            Text(
                text = "Password must be 10+ characters, include uppercase, number, and special character.",
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm password input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Instructional message
        Text(
            "This app is only for pre-registered users. Please enter your ID, phone number and password to claim your account.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        // Password strength feedback
        if (password.isNotBlank()) {
            Text(
                text = if (isPasswordValid(password)) "Strong Password" else "Weak Password",
                color = if (isPasswordValid(password)) Color.Green else Color.Red,
                fontSize = 12.sp
            )
        }

        // Error and success messages
        if (generalError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(generalError, color = Color.Red)
        }

        if (successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(successMessage, color = Color.Green)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button with validation and DB update
        Button(
            onClick = {
                generalError = ""
                when {
                    selectedUserId.isBlank() -> generalError = "Please select a User ID."
                    phoneNumber.isBlank() -> generalError = "Please enter your phone number."
                    password.isBlank() || confirmPassword.isBlank() -> generalError = "Please fill in both password fields."
                    password != confirmPassword -> generalError = "Passwords do not match."
                    !isPasswordValid(password) -> generalError = "Password must be at least 10 characters, include uppercase, lowercase, number, special character, and no spaces."
                    else -> {
                        // Coroutine to update password in Room
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(context)
                            val patient = db.patientDao().findPatientByIdAndPhone(selectedUserId, phoneNumber)
                            if (patient != null) {
                                db.patientDao().updatePassword(selectedUserId, password)
                                withContext(Dispatchers.Main) {
                                    successMessage = "Registration successful! Please login."
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    generalError = "User ID and Phone Number do not match our records."
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to navigate back to login screen
        Button(
            onClick = { navController.navigate("signin") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}