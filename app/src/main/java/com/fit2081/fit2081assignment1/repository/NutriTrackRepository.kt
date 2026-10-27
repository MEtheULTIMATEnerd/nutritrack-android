// NutriTrackRepository.kt
// This repository provides an abstraction layer over the AppDatabase,
// allowing ViewModels or other components to perform database operations
// without directly interacting with DAOs.

package com.fit2081.fit2081assignment1.repository

import com.fit2081.fit2081assignment1.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository class that handles all data operations for NutriTrack.
// Uses Kotlin coroutines and Dispatchers.IO to ensure operations are done on the IO thread.
class NutriTrackRepository(private val db: AppDatabase) {

    // ---------------- Patient Operations ----------------

    // Retrieves a Patient entity by their userId from the database.
    suspend fun getPatientById(userId: String): Patient? {
        return withContext(Dispatchers.IO) {
            db.patientDao().getPatientById(userId)
        }
    }

    // Inserts or updates a Patient entity in the database.
    suspend fun updatePatient(patient: Patient) {
        withContext(Dispatchers.IO) {
            db.patientDao().insertPatient(patient)
        }
    }

    // ---------------- Food Intake Operations ----------------

    // Retrieves FoodIntake data for a specific user by userId.
    suspend fun getFoodIntakeForUser(userId: String): FoodIntake? {
        return withContext(Dispatchers.IO) {
            db.foodIntakeDao().getFoodIntakeByUserId(userId)
        }
    }

    // ---------------- NutriCoach Tips ----------------

    // Inserts a NutriCoachTip into the database.
    suspend fun insertTip(tip: NutriCoachTip) {
        withContext(Dispatchers.IO) {
            db.nutriCoachTipDao().insertTip(tip)
        }
    }

    // Retrieves a list of NutriCoachTips for a specific user, sorted by latest.
    suspend fun getTipsForUser(userId: String): List<NutriCoachTip> {
        return withContext(Dispatchers.IO) {
            db.nutriCoachTipDao().getTipsForUser(userId)
        }
    }

    // Saves a generic motivational tip (not linked to user) into the database.
    suspend fun saveTip(tip: String) {
        db.motivationalTipDao().insertTip(MotivationalTip(tip = tip))
    }

    // Retrieves all generic motivational tips stored in the database.
    suspend fun getAllTips(): List<MotivationalTip> {
        return db.motivationalTipDao().getAllTips()
    }
}