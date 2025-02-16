package com.example.audiobookplayer.data

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.audiobookplayer.common.enums.BookStatus
import kotlinx.coroutines.flow.Flow

import androidx.room.util.SimpleSQLiteQuery
@Dao
interface AudiobookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: AudiobookEntity)

    @Update
    suspend fun update(book: AudiobookEntity)

    @Delete
    suspend fun delete(book: AudiobookEntity)

    @Query("SELECT * FROM audiobooks ORDER BY lastPlayed DESC")
    fun getPagedBooksByRecent(): PagingSource<Int, AudiobookEntity>

    @Query("SELECT * FROM audiobooks ORDER BY title COLLATE NOCASE ASC")
    fun getPagedBooksByTitle(): PagingSource<Int, AudiobookEntity>

    @Query("SELECT * FROM audiobooks ORDER BY (currentPosition * 1.0 / totalDuration) DESC")
    fun getPagedBooksByProgress(): PagingSource<Int, AudiobookEntity>

    @Query("SELECT * FROM audiobooks WHERE status = :status ORDER BY lastPlayed DESC")
    fun getBooksByStatus(status: BookStatus): Flow<List<AudiobookEntity>>

    @Query("SELECT * FROM audiobooks ORDER BY lastPlayed DESC")
    fun getBooksSortedByRecent(): Flow<List<AudiobookEntity>>

    @Query("SELECT * FROM audiobooks ORDER BY title COLLATE NOCASE ASC")
    fun getBooksSortedByTitle(): Flow<List<AudiobookEntity>>

    @Query("SELECT * FROM audiobooks ORDER BY (currentPosition * 1.0 / totalDuration) DESC")
    fun getBooksSortedByProgress(): Flow<List<AudiobookEntity>>

    @Query("SELECT * FROM audiobooks")
    fun getAllBooks(): Flow<List<AudiobookEntity>>

    @Query("DELETE FROM audiobooks")
    fun clearDatabase(): Int

    @Query("SELECT COUNT(*) FROM audiobooks WHERE folderPath = :folderPath")
    fun getBookCountInFolder(folderPath: String): Int

    @Query("SELECT IFNULL(SUM(fileSize), 0) FROM audiobooks WHERE folderPath = :folderPath")
    fun getTotalFileSizeInFolder(folderPath: String): Long

    @Query("SELECT DISTINCT folderPath FROM audiobooks")
    fun getAllLibraryFolders(): List<String>

    @Query("SELECT * FROM audiobooks WHERE isFavorite = 1 ORDER BY lastPlayed DESC")
    fun getFavoriteBooks(): List<AudiobookEntity>

    @Query("INSERT INTO bookmarks (audiobook_id, position, created_at) VALUES (:audiobookId, :position, :createdAt)")
    suspend fun addBookmark(audiobookId: String, position: Long, createdAt: Long)

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: String)

    @Query("UPDATE audiobooks SET sleepTimerEndTime = :endTime WHERE id = :bookId")
    suspend fun setSleepTimer(bookId: String, endTime: Long)

    @Query("SELECT sleepTimerEndTime FROM audiobooks WHERE id = :bookId")
    suspend fun getSleepTimerEndTime(bookId: String): Long?

    @Query("SELECT * FROM bookmarks WHERE audiobook_id = :audiobookId ORDER BY created_at DESC")
    fun getBookmarksForBook(audiobookId: String): Flow<List<Bookmark>>
    @Query("UPDATE audiobooks SET isFavorite = :isFavorite WHERE id = :bookId")
    fun setFavorite(bookId: String, isFavorite: Boolean): Int

    @Query("SELECT * FROM audiobooks WHERE id = :id")
    fun getBookById(id: String): Flow<AudiobookEntity?>

    @RawQuery
    suspend fun refreshLibrary(query: SimpleSQLiteQuery): Int {
        val pragmaQuery = SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)")
        return simpleQuery(pragmaQuery)
    }

    companion object {
        fun createSimpleQuery(sql: String, vararg args: Any?): SimpleSQLiteQuery {
            return SimpleSQLiteQuery(sql, args)
        }
    }
}
