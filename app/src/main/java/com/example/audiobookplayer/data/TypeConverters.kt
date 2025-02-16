package com.example.audiobookplayer.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.example.audiobookplayer.data.BookStatus

class TypeConverters {
    companion object {
        private val gson = Gson()
    }

    @TypeConverter
    @JvmStatic
    fun fromBookStatus(status: BookStatus): String {
        return status.name
    }

    @TypeConverter
    @JvmStatic
    fun toBookStatus(statusString: String): BookStatus {
        return BookStatus.valueOf(statusString)
    }

    @TypeConverter
    @JvmStatic
    fun fromAudioFiles(audioFiles: List<String>): String {
        return gson.toJson(audioFiles)
    }

    @TypeConverter
    @JvmStatic
    fun toAudioFiles(audioFilesString: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(audioFilesString, type)
    }
}
