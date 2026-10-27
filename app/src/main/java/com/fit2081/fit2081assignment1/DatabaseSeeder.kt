// DatabaseSeeder.kt
// This object is responsible for seeding the Room database with initial data from a CSV file
// on the first launch of the application. It reads from assets/UserDetail.csv and populates the
// Patient table in the database.

package com.fit2081.fit2081assignment1

import android.content.Context
import com.fit2081.fit2081assignment1.data.AppDatabase
import com.fit2081.fit2081assignment1.data.Patient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

object DatabaseSeeder {

    // Function to seed the database if it's empty. Runs on an IO coroutine.
    fun seedDatabaseIfNeeded(context: Context, db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val patientDao = db.patientDao()  // Get DAO for accessing Patient table
                val foodIntakeDao = db.foodIntakeDao()  // Unused here but prepared if needed

                // Check if any patient data already exists in the DB
                val count = patientDao.getPatientCount()
                if (count == 0) {
                    // Open the CSV file from assets
                    val inputStream = context.assets.open("UserDetail.csv")
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    reader.readLine()  // Skip the header line
                    var line: String?

                    // Read each line and convert it into a Patient object
                    while (reader.readLine().also { line = it } != null) {
                        val tokens = line!!.split(",")

                        // Ensure the CSV line has enough fields
                        if (tokens.size >= 17) {
                            val patient = Patient(
                                userId = tokens[1].trim(),         // User ID
                                phoneNumber = tokens[0].trim(),    // Phone number
                                name = "",                         // Empty for now
                                password = "",                     // Empty for now
                                sex = tokens[2].trim(),            // Sex
                                totalScoreMale = tokens[3].trim(),
                                totalScoreFemale = tokens[4].trim(),
                                discretionaryScore = tokens[5].trim(),
                                vegetableScore = tokens[6].trim(),
                                fruitScore = tokens[7].trim(),
                                grainScore = tokens[8].trim(),
                                meatScore = tokens[9].trim(),
                                dairyScore = tokens[10].trim(),
                                waterScore = tokens[11].trim(),
                                fatsScore = tokens[12].trim(),
                                sodiumScore = tokens[13].trim(),
                                addedSugarsScore = tokens[14].trim(),
                                alcoholScore = tokens[15].trim()
                            )

                            // Insert the patient record into the database
                            patientDao.insertPatient(patient)
                        }
                    }

                    // Close the file resources
                    reader.close()
                    inputStream.close()
                }
            } catch (e: Exception) {
                // Print the exception stack trace for debugging
                e.printStackTrace()
            }
        }
    }
}