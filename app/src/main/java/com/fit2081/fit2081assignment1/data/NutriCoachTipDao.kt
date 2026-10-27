// NutriCoachTipDao.kt
// This DAO (Data Access Object) interface provides methods to interact with the "nutri_coach_tip" table in the Room database.
// It allows insertion of new tips and retrieval of tips for a specific user in reverse chronological order.

package com.fit2081.fit2081assignment1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NutriCoachTipDao {

    // Inserts a new NutriCoach tip into the database.
    // `suspend` makes this function callable from a coroutine.
    @Insert
    suspend fun insertTip(tip: NutriCoachTip)

    // Retrieves all NutriCoach tips for a given user, ordered by timestamp in descending order (most recent first).
    // `:userId` is a parameter passed into the SQL query to filter tips by user.
    @Query("SELECT * FROM nutri_coach_tip WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getTipsForUser(userId: String): List<NutriCoachTip>
}