// PatientDao.kt
// This interface defines data access operations (DAO) for the `patient` table in the Room database.
// It supports inserting, querying, updating, and deleting patient records.

package com.fit2081.fit2081assignment1.data

import androidx.room.*

// Marks this interface as a DAO (Data Access Object) for Room.
@Dao
interface PatientDao {

    // Inserts a patient into the database. Replaces the record if the userId already exists.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    // Retrieves a patient by their userId.
    @Query("SELECT * FROM patient WHERE userId = :userId")
    suspend fun getPatientById(userId: String): Patient?

    // Counts the number of patient records in the table.
    @Query("SELECT COUNT(*) FROM patient")
    suspend fun getPatientCount(): Int

    // Retrieves a list of all user IDs stored in the database.
    @Query("SELECT userId FROM patient")
    suspend fun getAllUserIds(): List<String>

    // Authenticates a user by matching both userId and password.
    @Query("SELECT * FROM patient WHERE userId = :userId AND password = :password")
    suspend fun authenticateUser(userId: String, password: String): Patient?

    // Finds a patient by matching userId and phone number.
    @Query("SELECT * FROM patient WHERE userId = :userId AND phoneNumber = :phone")
    suspend fun findPatientByIdAndPhone(userId: String, phone: String): Patient?

    // Updates the password of a patient with the specified userId.
    @Query("UPDATE patient SET password = :newPassword WHERE userId = :userId")
    suspend fun updatePassword(userId: String, newPassword: String)

    // Updates the phone number of a patient with the specified userId.
    @Query("UPDATE patient SET phoneNumber = :newPhone WHERE userId = :userId")
    suspend fun updatePhoneNumber(userId: String, newPhone: String)

    // Deletes a specific patient record from the database.
    @Delete
    suspend fun deletePatient(patient: Patient)

    // Retrieves a list of all patients.
    @Query("SELECT * FROM patient")
    suspend fun getAllPatients(): List<Patient>

    // Checks if a patient with the specified userId exists.
    @Query("SELECT EXISTS(SELECT 1 FROM patient WHERE userId = :userId)")
    suspend fun doesUserExist(userId: String): Boolean
}