/**
 * FoodQuestionnaireScreen.kt
 *
 * This file manages the multi-step food questionnaire used in the NutriTrack app.
 * It includes the ViewModel to hold questionnaire data and composable screens to:
 *   - Select food categories
 *   - Choose eating persona (with details and image)
 *   - Provide daily routine timing (meal, sleep, wake)
 *
 * The user's total score and detailed dietary scores are calculated using Room DB data
 * and displayed on the Home and Insights screens.
 */

package com.fit2081.fit2081assignment1

// Imports for UI, navigation, and lifecycle handling
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.fit2081.fit2081assignment1.data.Patient
import com.fit2081.fit2081assignment1.repository.NutriTrackRepository
import kotlinx.coroutines.*

/**
 * ViewModelFactory for creating the FoodQuestionnaireViewModel with a repository.
 */
class FoodQuestionnaireViewModelFactory(private val repository: NutriTrackRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodQuestionnaireViewModel::class.java)) {
            return FoodQuestionnaireViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * ViewModel to store questionnaire inputs and handle Room DB operations.
 */
class FoodQuestionnaireViewModel(private val repository: NutriTrackRepository) : ViewModel() {
    // Questionnaire state
    var selectedCategories = mutableStateOf(setOf<String>())
    var selectedPersona = mutableStateOf("")
    var biggestMealTime = mutableStateOf("")
    var sleepTime = mutableStateOf("")
    var wakeupTime = mutableStateOf("")

    // CSV / Patient DB info
    var userId = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var sex = mutableStateOf("")
    var userScoreMale = mutableStateOf("")
    var userScoreFemale = mutableStateOf("")
    var userScore = mutableStateOf("")

    val patientData = mutableStateOf<Patient?>(null)
    val totalScore = mutableStateOf("N/A")
    val detailedScores = mutableStateOf<Map<String, String>>(emptyMap())

    /**
     * Load the patient data and update total & detailed scores.
     */
    fun loadPatient(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val patient = repository.getPatientById(userId)
            withContext(Dispatchers.Main) {
                patientData.value = patient
            }

            patient?.let {
                totalScore.value = if (it.sex.equals("Female", ignoreCase = true)) {
                    it.totalScoreFemale ?: "N/A"
                } else {
                    it.totalScoreMale ?: "N/A"
                }

                detailedScores.value = mapOf(
                    "Discretionary foods" to (it.discretionaryScore ?: "0"),
                    "Vegetables" to (it.vegetableScore ?: "0"),
                    "Fruits" to (it.fruitScore ?: "0"),
                    "Grains and Cereals" to (it.grainScore ?: "0"),
                    "Meat and Alternatives" to (it.meatScore ?: "0"),
                    "Dairy and Alternatives" to (it.dairyScore ?: "0"),
                    "Water" to (it.waterScore ?: "0"),
                    "Fats" to (it.fatsScore ?: "0"),
                    "Sodium" to (it.sodiumScore ?: "0"),
                    "Added sugars" to (it.addedSugarsScore ?: "0"),
                    "Alcohol" to (it.alcoholScore ?: "0")
                )
            }
        }
    }

    fun saveFoodCategory(categories: Set<String>) {
        selectedCategories.value = categories
    }

    fun savePersona(persona: String) {
        selectedPersona.value = persona
    }

    fun saveMealTimes(biggestMeal: String, sleep: String, wake: String) {
        biggestMealTime.value = biggestMeal
        sleepTime.value = sleep
        wakeupTime.value = wake
        Log.d("MealTimingScreen", "Saved: $biggestMeal, $sleep, $wake")
    }

    fun calculateFoodScore(): String {
        return if (sex.value.equals("Female", ignoreCase = true)) {
            userScoreFemale.value
        } else {
            userScoreMale.value
        }
    }
}

/**
 * FoodQuestionnaireScreen orchestrates the multi-step questionnaire.
 */
@Composable
fun FoodQuestionnaireScreen(navController: NavController, viewModel: FoodQuestionnaireViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        loadQuestionnaireData(context, viewModel)
    }

    var currentStep by remember { mutableStateOf(1) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (currentStep) {
            1 -> FoodCategoryScreen(viewModel) { currentStep++ }
            2 -> PersonaSelectionScreen(viewModel) { currentStep++ }
            3 -> MealTimingScreen(viewModel) {
                viewModel.userScore.value = viewModel.calculateFoodScore()
                navController.navigate("home")
            }
        }
    }
}

/**
 * FoodCategoryScreen: lets user select multiple food categories using checkboxes.
 */
@Composable
fun FoodCategoryScreen(viewModel: FoodQuestionnaireViewModel, onNext: () -> Unit) {
    val selectedCategories = viewModel.selectedCategories.value
    val categories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Dairy", "Fish", "Nuts/Seeds")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("What types of food do you usually eat?", style = MaterialTheme.typography.titleLarge)

        categories.forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Checkbox(
                    checked = selectedCategories.contains(category),
                    onCheckedChange = {
                        val updated = if (it) selectedCategories + category else selectedCategories - category
                        viewModel.saveFoodCategory(updated)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = category)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onNext() }, enabled = selectedCategories.isNotEmpty()) {
            Text("Next")
        }
    }
}

/**
 * PersonaSelectionScreen: displays persona cards with tap-to-expand dialog.
 */
@Composable
fun PersonaSelectionScreen(viewModel: FoodQuestionnaireViewModel, onNext: () -> Unit) {
    var selectedPersona by remember { mutableStateOf(viewModel.selectedPersona.value.ifEmpty { null }) }
    var selectedDescription by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val personas = listOf(
        "Health Devotee" to "I’m passionate about healthy eating...",
        "Mindful Eater" to "I’m health-conscious...",
        "Wellness Striver" to "I aspire to be healthy...",
        "Balance Seeker" to "I try and live a balanced lifestyle...",
        "Health Procrastinator" to "I’m contemplating healthy eating...",
        "Food Carefree" to "I’m not bothered about healthy eating."
    )

    val personaImages = mapOf(
        "Health Devotee" to R.drawable.persona_1,
        "Mindful Eater" to R.drawable.persona_2,
        "Wellness Striver" to R.drawable.persona_3,
        "Balance Seeker" to R.drawable.persona_4,
        "Health Procrastinator" to R.drawable.persona_5,
        "Food Carefree" to R.drawable.persona_6
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Your Eating Persona", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        personas.forEach { (personaName, description) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        selectedPersona = personaName
                        selectedDescription = description
                        showDialog = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPersona == personaName) Color.LightGray else Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(personaName, fontWeight = FontWeight.Bold)
                    Text(description.take(50) + "...", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.savePersona(selectedPersona!!); onNext() }, enabled = selectedPersona != null) {
            Text("Next")
        }
    }

    if (showDialog && selectedPersona != null && selectedDescription != null) {
        PersonaDetailDialog(
            personaName = selectedPersona!!,
            description = selectedDescription!!,
            imageResId = personaImages[selectedPersona] ?: R.drawable.persona_1,
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * PersonaDetailDialog: shows expanded persona description and image.
 */
@Composable
fun PersonaDetailDialog(personaName: String, description: String, imageResId: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(personaName) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = imageResId), contentDescription = null, modifier = Modifier.size(150.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(description)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * MealTimingScreen: collects biggest meal, sleep, and wake times.
 */
@Composable
fun MealTimingScreen(viewModel: FoodQuestionnaireViewModel, onNext: () -> Unit) {
    val context = LocalContext.current

    var biggestMealTime by remember { mutableStateOf(viewModel.biggestMealTime.value.ifEmpty { "00:00 PM" }) }
    var sleepTime by remember { mutableStateOf(viewModel.sleepTime.value.ifEmpty { "00:00 PM" }) }
    var wakeTime by remember { mutableStateOf(viewModel.wakeupTime.value.ifEmpty { "00:00 AM" }) }

    var errorMessage by remember { mutableStateOf("") }

    fun validateTimes(): Boolean {
        return when {
            sleepTime == wakeTime -> { errorMessage = "Sleep and Wake time can't be the same."; false }
            sleepTime == biggestMealTime -> { errorMessage = "Sleep and Meal time can't be the same."; false }
            wakeTime == biggestMealTime -> { errorMessage = "Wake and Meal time can't be the same."; false }
            else -> { errorMessage = ""; true }
        }
    }

    fun showTimePicker(initialTime: String, onTimeSelected: (String) -> Unit) {
        TimePickerDialog(context, { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            val formatted = String.format("%02d:%02d %s", hour12, minute, amPm)
            onTimeSelected(formatted)
        }, 12, 0, false).show()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tell us about your daily routine", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        TimeInputField("What time do you eat your biggest meal?", biggestMealTime) { showTimePicker(biggestMealTime) { biggestMealTime = it } }
        TimeInputField("What time do you go to sleep?", sleepTime) { showTimePicker(sleepTime) { sleepTime = it } }
        TimeInputField("What time do you wake up?", wakeTime) { showTimePicker(wakeTime) { wakeTime = it } }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (validateTimes()) {
                viewModel.saveMealTimes(biggestMealTime, sleepTime, wakeTime)
                saveQuestionnaireData(context, viewModel)
                onNext()
            }
        }) {
            Text("Finish")
        }
    }
}

/**
 * TimeInputField is a reusable field for selecting time.
 */
@Composable
fun TimeInputField(label: String, time: String, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color.Gray)) {
            Icon(Icons.Outlined.AccessTime, contentDescription = "Clock")
            Spacer(modifier = Modifier.width(8.dp))
            Text(time)
        }
    }
}