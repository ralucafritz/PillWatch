package com.example.pillwatch.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.MetadataEntity
import com.example.pillwatch.data.model.UserEntity

@Database(
    entities = [MedsEntity::class,
                       MetadataEntity::class,
                        UserEntity::class],
    version = 2,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val medsDao: MedsDao
    abstract val metadataDao: MetadataDao
    abstract val userDao: UserDao

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