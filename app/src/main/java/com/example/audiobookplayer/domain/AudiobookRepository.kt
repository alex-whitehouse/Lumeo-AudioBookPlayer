package com.example.audiobookplayer.domain

import androidx.paging.PagingSource
import com.example.audiobookplayer.data.AudiobookEntity
import kotlinx.coroutines.flow.Flow

interface AudiobookRepository {
    suspend fun upsertBook(book: AudiobookEntity)
    
    fun getPagedBooksSortedByRecent(): PagingSource<Int, AudiobookEntity>
    fun getPagedBooksSortedByTitle(): PagingSource<Int, AudiobookEntity>
    fun getPagedBooksSortedByProgress(): PagingSource<Int, AudiobookEntity>
    
    fun getBooksFilteredByStatus(status: BookStatus): Flow<List<AudiobookEntity>>
    suspend fun getBookById(id: String): AudiobookEntity?
    suspend fun deleteBook(id: String)
}
