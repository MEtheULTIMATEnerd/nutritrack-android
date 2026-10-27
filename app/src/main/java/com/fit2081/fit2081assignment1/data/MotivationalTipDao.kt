// MotivationalTipDao.kt
// This file defines a Data Access Object (DAO) interface for interacting with the "motivational_tips" table in Room.
// It provides methods to insert and retrieve motivational tips used in the NutriCoach feature.

package com.fit2081.fit2081assignment1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Marks this interface as a DAO for Room database operations.
@Dao
interface MotivationalTipDao {

    // Inserts a single motivational tip into the database.
    // If a tip with the same primary key already exists, it will be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: MotivationalTip)

    // Retrieves all motivational tips stored in the database.
    @Query("SELECT * FROM motivational_tips")
    suspend fun getAllTips(): List<MotivationalTip>
}