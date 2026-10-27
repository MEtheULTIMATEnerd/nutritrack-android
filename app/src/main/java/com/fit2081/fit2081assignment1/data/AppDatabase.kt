// AppDatabase.kt
// This file defines the Room database for the NutriTrack application. It sets up all DAOs and entities
// used in the local database, and provides a singleton instance for accessing the database across the app.

package com.fit2081.fit2081assignment1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Annotates the class to be a Room Database with the listed entity classes and database version.
// Entities represent the tables in the database.
@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTip::class, MotivationalTip::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    // Abstract DAOs for interacting with the tables.
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachTipDao(): NutriCoachTipDao
    abstract fun motivationalTipDao(): MotivationalTipDao

    companion object {
        // Volatile ensures visibility of changes to INSTANCE across threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Returns the singleton instance of the AppDatabase.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Build the Room database and assign it to the INSTANCE.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutritrack_db" // Name of the database file.
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}