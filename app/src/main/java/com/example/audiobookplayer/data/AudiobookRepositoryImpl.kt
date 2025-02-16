package com.example.audiobookplayer.data

import androidx.paging.PagingSource
import com.example.audiobookplayer.domain.AudiobookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AudiobookRepositoryImpl @Inject constructor(
    private val dao: AudiobookDao
) : AudiobookRepository {

    override suspend fun upsertBook(book: AudiobookEntity) {
        dao.upsert(book)
    }

    override fun getPagedBooksSortedByRecent(): PagingSource<Int, AudiobookEntity> {
        return dao.getPagedBooksByRecent()
    }

    override fun getPagedBooksSortedByTitle(): PagingSource<Int, AudiobookEntity> {
        return dao.getPagedBooksByTitle()
    }

    override fun getPagedBooksSortedByProgress(): PagingSource<Int, AudiobookEntity> {
        return dao.getPagedBooksByProgress()
    }

    override fun getBooksFilteredByStatus(status: BookStatus): Flow<List<AudiobookEntity>> {
        return dao.getBooksByStatus(status)
    }

    override suspend fun getBookById(id: String): AudiobookEntity? {
        return dao.getBookById(id)
    }

    override suspend fun deleteBook(id: String) {
        dao.deleteBook(id)
    }
}
