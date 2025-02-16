package com.example.audiobookplayer.data

import androidx.paging.PagingSource
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.domain.AudiobookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AudiobookRepositoryImpl @Inject constructor(
    private val dao: AudiobookDao
) : AudiobookRepository {

    override fun getPagedBooksSortedByRecent() = dao.getPagedBooksByRecent()

    override fun getPagedBooksSortedByTitle() = dao.getPagedBooksByTitle()

    override fun getPagedBooksSortedByProgress() = dao.getPagedBooksByProgress()

    override fun getAllAudiobooks(): Flow<List<AudiobookEntity>> {
        return dao.getAllBooks()
    }

    override fun getAudiobooksByStatus(status: BookStatus): Flow<List<AudiobookEntity>> {
        return dao.getBooksByStatus(status)
    }

    override fun getSortedAudiobooks(sortOrder: String): Flow<List<AudiobookEntity>> {
        return when (sortOrder) {
            "recent" -> dao.getBooksSortedByRecent()
            "title" -> dao.getBooksSortedByTitle()
            "progress" -> dao.getBooksSortedByProgress()
            else -> dao.getAllBooks()
        }
    }

    override suspend fun insertAudiobook(audiobook: AudiobookEntity) {
        dao.insert(audiobook)
    }

    override suspend fun updateAudiobook(audiobook: AudiobookEntity) {
        dao.update(audiobook)
    }

    override suspend fun deleteAudiobook(id: Int) {
        dao.delete(id)
    }

    // Sleep Timer Implementation
    override suspend fun setSleepTimer(id: Int, endTime: Long) {
        dao.setSleepTimer(id, endTime)
    }

    override suspend fun getSleepTimerEndTime(id: Int): Long? {
        return dao.getSleepTimerEndTime(id)
    }

    // Bookmark Implementation
    override suspend fun addBookmark(id: Int, position: Long) {
        val currentTime = System.currentTimeMillis()
        dao.addBookmark(id, position, currentTime)
    }

    override fun getBookmarksForBook(id: Int): Flow<List<Bookmark>> {
        return dao.getBookmarksForBook(id)
    }

    override suspend fun refreshLibrary() {
        // Trigger library refresh through DAO
        dao.refreshLibrary()
    }
}
