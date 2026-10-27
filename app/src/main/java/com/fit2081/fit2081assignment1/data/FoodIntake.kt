// FoodIntake.kt
// This file defines the data class FoodIntake, which represents the user's food intake scores.
// It is used as an entity in the Room database to store detailed dietary information for each user.

package com.fit2081.fit2081assignment1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Marks this data class as a Room database entity with the table name "food_intake"
@Entity(tableName = "food_intake")
data class FoodIntake(
    // Primary key to uniquely identify each user's food intake record
    @PrimaryKey val userId: String,

    // Total food quality scores separated by sex
    val totalScoreMale: String,
    val totalScoreFemale: String,

    // Scores for each dietary category (based on intake levels or recommendations)
    val discretionary: String,     // Discretionary foods score
    val vegetables: String,        // Vegetable intake score
    val fruits: String,            // Fruit intake score
    val grains: String,            // Grains and cereals intake score
    val meat: String,              // Meat and alternatives score
    val dairy: String,             // Dairy and alternatives score
    val water: String,             // Water intake score
    val fats: String,              // Unsaturated fats score
    val sodium: String,            // Sodium intake score
    val addedSugars: String,       // Added sugars score
    val alcohol: String            // Alcohol consumption score
)