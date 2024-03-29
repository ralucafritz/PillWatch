package com.example.pillwatch.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.data.model.MetadataEntity
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.data.model.UserMedsEntity

@Database(
    entities = [
        MedsEntity::class,
        MetadataEntity::class,
        UserEntity::class,
        AlarmEntity::class,
        MedsLogEntity::class,
        UserMedsEntity::class],
    version =9,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val medsDao: MedsDao
    abstract val metadataDao: MetadataDao
    abstract val userDao: UserDao
    abstract val alarmDao: AlarmDao
    abstract val medsLogDao: MedsLogDao
    abstract val userMedsDao: UserMedsDao

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
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onOpen(db: SupportSQLiteDatabase) {
                                super.onOpen(db)
                                db.execSQL("PRAGMA foreign_keys=ON;")
                                db.setForeignKeyConstraintsEnabled(true)
                                db.enableWriteAheadLogging()
                            }
                        })
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}