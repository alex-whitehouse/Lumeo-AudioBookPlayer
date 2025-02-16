package com.example.audiobookplayer.data

import androidx.room.TypeConverter
import com.example.audiobookplayer.common.enums.BookStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

object TypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toBookStatus(statusString: String): BookStatus {
        return BookStatus.valueOf(statusString)
    }

    @TypeConverter
    fun fromBookStatus(bookStatus: BookStatus): String {
        return bookStatus.name
    }

    @TypeConverter
    fun fromAudioFiles(audioFiles: List<String>): String {
        return gson.toJson(audioFiles)
    }

    @TypeConverter
    fun toAudioFiles(audioFilesString: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(audioFilesString, type)
    }
}
