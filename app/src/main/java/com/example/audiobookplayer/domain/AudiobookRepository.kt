package com.example.audiobookplayer.domain

import androidx.paging.PagingSource
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.data.AudiobookEntity
import kotlinx.coroutines.flow.Flow

interface AudiobookRepositoryInterface : com.example.audiobookplayer.data.AudiobookRepository {
    suspend fun upsertBook(book: AudiobookEntity)
    
    fun getPagedBooksSortedByRecent(): PagingSource<Int, AudiobookEntity>
    fun getPagedBooksSortedByTitle(): PagingSource<Int, AudiobookEntity>
    fun getPagedBooksSortedByProgress(): PagingSource<Int, AudiobookEntity>
    
    fun getBooksFilteredByStatus(status: BookStatus): Flow<List<AudiobookEntity>>
    suspend fun getBookById(id: String): AudiobookEntity?
    suspend fun deleteBook(id: String)

    // Sleep Timer Methods
    suspend fun setSleepTimer(bookId: String, endTime: Long)
    suspend fun getSleepTimerEndTime(bookId: String): Long?

    // Bookmark Methods
    suspend fun addBookmark(audiobookId: String, position: Long)
    fun getBookmarksForBook(audiobookId: String): Flow<List<Bookmark>>
}
