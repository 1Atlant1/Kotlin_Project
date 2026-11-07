package com.example.kotlin_project.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Dish::class, InsulinProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dishDao(): DishDao
    abstract fun insulinProfileDao(): InsulinProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "everydaydia_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(
                                        database.dishDao(),
                                        database.insulinProfileDao()
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(dishDao: DishDao, insulinDao: InsulinProfileDao) {
            DatabaseSeeder.initialDishes.forEach { dish -> dishDao.insertDish(dish) }
            DatabaseSeeder.initialInsulins.forEach { insulin -> insulinDao.insertProfile(insulin) }
        }
    }
}