// FoodIntakeDao.kt
// This file defines the DAO (Data Access Object) interface for interacting with the `food_intake` table.
// It provides methods to insert and retrieve food intake data using Room persistence library.

package com.fit2081.fit2081assignment1.data

import androidx.room.*

// Marks this interface as a DAO for Room to generate necessary code for database operations
@Dao
interface FoodIntakeDao {

    // Inserts a FoodIntake entry into the database.
    // If a record with the same userId already exists, it will be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    // Queries the database for the FoodIntake entry associated with a specific userId.
    // Returns null if no entry exists.
    @Query("SELECT * FROM food_intake WHERE userId = :userId")
    suspend fun getFoodIntakeByUserId(userId: String): FoodIntake?
}