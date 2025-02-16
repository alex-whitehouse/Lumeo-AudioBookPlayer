package com.example.audiobookplayer.data

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AudiobookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(book: AudiobookEntity): Long

    @Query("""
        SELECT * FROM audiobooks 
        WHERE (:status IS NULL OR status = :status)
        ORDER BY lastPlayed DESC
    """)
    fun getPagedBooksByRecent(status: BookStatus? = null): PagingSource<Int, AudiobookEntity>

    @Query("""
        SELECT * FROM audiobooks 
        WHERE (:status IS NULL OR status = :status)
        ORDER BY title COLLATE NOCASE ASC
    """)
    fun getPagedBooksByTitle(status: BookStatus? = null): PagingSource<Int, AudiobookEntity>

    @Query("""
        SELECT * FROM audiobooks 
        WHERE (:status IS NULL OR status = :status)
        ORDER BY (currentPosition * 1.0 / totalDuration) DESC
    """)
    fun getPagedBooksByProgress(status: BookStatus? = null): PagingSource<Int, AudiobookEntity>

    @Query("SELECT * FROM audiobooks WHERE status = :status ORDER BY lastPlayed DESC")
    fun getBooksByStatus(status: BookStatus): Flow<List<AudiobookEntity>>

    @Query("SELECT * FROM audiobooks WHERE id = :id")
    fun getBookById(id: String): Flow<AudiobookEntity?>

    @Query("DELETE FROM audiobooks WHERE id = :id")
    fun deleteBook(id: String): Int
}
