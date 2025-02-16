package com.example.audiobookplayer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "audiobooks",
    indices = [
        Index(value = ["status"]),
        Index(value = ["last_played"]),
        Index(value = ["author"], unique = false),
        Index(value = ["folder_path"], unique = true)
    ]
)
data class Audiobook(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "folder_path")
    val folderPath: String,

    @ColumnInfo(name = "cover_uri")
    val coverUri: String? = null,

    @ColumnInfo(name = "current_position", defaultValue = "0")
    val currentPosition: Long = 0L,

    @ColumnInfo(name = "total_duration")
    val totalDuration: Long,

    @ColumnInfo(name = "last_played")
    val lastPlayed: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "playback_speed", defaultValue = "1.0")
    val playbackSpeed: Float = 1.0f,

    @ColumnInfo(name = "status")
    val status: BookStatus = BookStatus.NEW,

    // Metadata fields
    @ColumnInfo(name = "author")
    val author: String? = null,

    @ColumnInfo(name = "genre")
    val genre: String? = null,

    @ColumnInfo(name = "narrator")
    val narrator: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "file_count")
    val fileCount: Int = 1
)

enum class BookStatus {
    NEW,
    STARTED,
    FINISHED
}
