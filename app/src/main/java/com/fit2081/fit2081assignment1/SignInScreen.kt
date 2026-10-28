/**
 * SignInScreen.kt
 *
 * This composable function displays the sign-in screen for the NutriTrack app.
 * It provides input fields for the user ID and phone number, shows an error message if needed,
 * and validates the credentials when the "Continue" button is pressed.
 * If the credentials are valid (using the CSV data via validateUserInput), the user is navigated
 * to the Food Questionnaire screen.
 */
package com.fit2081.fit2081assignment1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit2081.fit2081assignment1.data.AppDatabase
import com.fit2081.fit2081assignment1.ui.theme.FIT2081Assignment1Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    val context = LocalContext.current

    val userIds = remember { mutableStateListOf<String>() }
    var selectedUserId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf("") }

    // Load User IDs from Room DB
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val ids = db.patientDao().getAllUserIds()
        userIds.addAll(ids)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Log in", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // User ID Dropdown
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedUserId,
                onValueChange = {},
                readOnly = true,
                label = { Text("My ID") },
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

        // Password Field with Eye Icon
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Informational Text
        Text(
            text = "This app is only for pre-registered users.\nPlease enter your ID and password or Register to claim your account.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        if (generalError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(generalError, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    selectedUserId.isBlank() -> generalError = "Please select a User ID."
                    password.isBlank() -> generalError = "Please enter your password."
                    else -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(context)
                            val patient = db.patientDao().getPatientById(selectedUserId)
                            if (patient != null && PasswordHasher.verify(password, patient.password)) {
                                // Save session
                                saveSession(context, selectedUserId)
                                withContext(Dispatchers.Main) {
                                    navController.navigate("food_questionnaire")
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    generalError = "Invalid credentials. Please try again."
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}
