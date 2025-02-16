package com.example.audiobookplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.audiobookplayer.common.enums.BookStatus

@Entity(tableName = "audiobooks")
data class AudiobookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String?,
    val coverUri: String?,
    val totalDuration: Long,
    val currentPosition: Long = 0,
    val lastPlayed: Long = System.currentTimeMillis(),
    val status: BookStatus = BookStatus.NEW,
    @TypeConverters(TypeConverters::class)
    val audioFiles: List<String> // URIs of audio files
)
