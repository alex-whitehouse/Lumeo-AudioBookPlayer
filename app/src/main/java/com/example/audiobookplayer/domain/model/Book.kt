package com.example.audiobookplayer.domain.model

import com.example.audiobookplayer.common.enums.BookStatus

data class Book(
    val id: String,
    val folderPath: String,
    val coverUri: String?,
    val currentPosition: Long,
    val totalDuration: Long,
    val lastPlayed: Long,
    val playbackSpeed: Float,
    val status: BookStatus,
    val author: String?,
    val genre: String?,
    val narrator: String?,
    val createdAt: Long,
    val fileCount: Int
)
