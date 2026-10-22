package com.example.kotlin_project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    @Query("SELECT DISTINCT category FROM dishes ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM dishes WHERE category = :category ORDER BY name ASC")
    fun getDishesByCategory(category: String): Flow<List<Dish>>

    @Query("SELECT * FROM dishes WHERE id = :id")
    fun getDishById(id: Int): Flow<Dish?>

    @Query("SELECT COUNT(*) FROM dishes WHERE category = :category")
    fun getDishCountByCategory(category: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDish(dish: Dish)

    @Query("DELETE FROM dishes WHERE id = :id")
    suspend fun deleteDish(id: Int)
}