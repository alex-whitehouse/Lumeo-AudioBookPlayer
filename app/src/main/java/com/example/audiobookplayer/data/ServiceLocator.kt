package com.example.audiobookplayer.data

import android.content.Context
import com.example.audiobookplayer.data.AudiobookRepository
import com.example.audiobookplayer.data.AudiobookRepositoryImpl
import com.example.audiobookplayer.data.AppDatabase

object ServiceLocator {

    private var database: AppDatabase? = null
    private var repository: AudiobookRepository? = null

    fun provideAudiobookRepository(context: Context): AudiobookRepository {
        return repository ?: synchronized(this) {
            val repo = AudiobookRepositoryImpl(
                audiobookDao = getDatabase(context).audiobookDao(),
                preferencesManager = PreferencesManager(context)
            )
            repository = repo
            return repo
        }
    }

    private fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context)
            database = db
            return db
        }
    }
}
