// Patient.kt
// This file defines the Patient data entity for the Room database.
// Each instance represents a single patient record, storing their profile and nutrition score data.

package com.fit2081.fit2081assignment1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This annotation marks the class as a Room entity with the table name "patient".
@Entity(tableName = "patient")
data class Patient(
    @PrimaryKey val userId: String,                  // Unique identifier for each user (primary key).
    val phoneNumber: String,                         // Patient's phone number.
    val name: String,                                // Patient's name.
    val password: String,                            // PBKDF2 password hash for claimed demo accounts.
    val sex: String,                                 // Patient's sex (used to determine which score to display).

    // Optional nutritional scores (nullable) based on gender and food category.
    val totalScoreMale: String?,                     // Total food quality score for male patients.
    val totalScoreFemale: String?,                   // Total food quality score for female patients.
    val vegetableScore: String?,                     // Score for vegetable intake.
    val fruitScore: String?,                         // Score for fruit intake.
    val grainScore: String?,                         // Score for grain and cereals intake.
    val meatScore: String?,                          // Score for meat and alternatives intake.
    val dairyScore: String?,                         // Score for dairy intake.
    val waterScore: String?,                         // Score for water consumption.
    val fatsScore: String?,                          // Score for fats (preferably healthy fats).
    val sodiumScore: String?,                        // Score for sodium (lower is better).
    val addedSugarsScore: String?,                   // Score for added sugars (lower is better).
    val alcoholScore: String?,                       // Score for alcohol consumption.
    val discretionaryScore: String?                  // Score for discretionary or unhealthy foods.
)
