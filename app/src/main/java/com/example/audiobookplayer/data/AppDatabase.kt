package com.example.audiobookplayer.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters

@Database(
    entities = [AudiobookEntity::class, Bookmark::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(com.example.audiobookplayer.data.TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audiobookDao(): AudiobookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "audiobook_database"
                ).fallbackToDestructiveMigration()
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate database if needed
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
