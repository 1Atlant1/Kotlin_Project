package com.example.kotlin_project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InsulinProfileDao {
    @Query("SELECT * FROM insulin_profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<InsulinProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: InsulinProfile)
}