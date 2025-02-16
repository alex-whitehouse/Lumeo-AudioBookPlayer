package com.example.audiobookplayer.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = Audiobook::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("audiobook_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["audiobook_id"])]
)
data class Bookmark(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "audiobook_id")
    val audiobookId: String,

    @ColumnInfo(name = "position")
    val position: Long,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
