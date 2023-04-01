package com.example.pillwatch.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MedDataEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val databaseDao: DatabaseDao

    companion object {
        @Volatile //  = value of INSTANCE is up to date and the same to all threads
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // need synchronized in case multiple threads want to access the database
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "pill_watch_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }


}