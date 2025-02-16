package com.example.audiobookplayer.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.audiobookplayer.data.TypeConverters as DataConverters

@Database(
    entities = [AudiobookEntity::class, Bookmark::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DataConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audiobookDao(): AudiobookDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE audiobooks ADD COLUMN folderPath TEXT")
                database.execSQL("ALTER TABLE audiobooks ADD COLUMN fileSize INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE audiobooks ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "audiobook_database"
                ).addMigrations(MIGRATION_1_2)
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
