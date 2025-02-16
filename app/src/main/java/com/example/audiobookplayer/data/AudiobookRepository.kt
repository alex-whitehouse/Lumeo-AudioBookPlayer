package com.example.audiobookplayer.data

import kotlinx.coroutines.flow.Flow

interface AudiobookRepository {
    fun getAllAudiobooks(): Flow<List<AudiobookEntity>>
    fun getAudiobooksByStatus(status: AudiobookEntity.BookStatus): Flow<List<AudiobookEntity>>
    fun getSortedAudiobooks(sortOrder: String): Flow<List<AudiobookEntity>>
    
    // New methods for library management
    fun getAllLibraryFolders(): Flow<List<String>>
    fun getBookCountInFolder(folderPath: String): Flow<Int>
    fun getTotalFileSizeInFolder(folderPath: String): Flow<Long>
    fun getFavoriteBooks(): Flow<List<AudiobookEntity>>
    suspend fun setFavorite(bookId: Int, isFavorite: Boolean)
    
    suspend fun insertAudiobook(audiobook: AudiobookEntity)
    suspend fun updateAudiobook(audiobook: AudiobookEntity)
    suspend fun deleteAudiobook(id: Int)
}
