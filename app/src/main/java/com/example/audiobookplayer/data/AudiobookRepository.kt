package com.example.audiobookplayer.data

import androidx.paging.PagingSource
import com.example.audiobookplayer.common.enums.BookStatus
import kotlinx.coroutines.flow.Flow

interface AudiobookRepository {
    fun getPagedBooksSortedByRecent(): PagingSource<Int, AudiobookEntity>
    fun getPagedBooksSortedByTitle(): PagingSource<Int, AudiobookEntity>
    fun getPagedBooksSortedByProgress(): PagingSource<Int, AudiobookEntity>
    
    fun getAllAudiobooks(): Flow<List<AudiobookEntity>>
    fun getAudiobooksByStatus(status: BookStatus): Flow<List<AudiobookEntity>>
    fun getSortedAudiobooks(sortOrder: String): Flow<List<AudiobookEntity>>

    suspend fun insertAudiobook(audiobook: AudiobookEntity)
    suspend fun updateAudiobook(audiobook: AudiobookEntity)
    suspend fun deleteAudiobook(id: Int)
    
    suspend fun refreshLibrary()
}
