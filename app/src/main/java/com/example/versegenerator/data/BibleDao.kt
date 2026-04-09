package com.example.versegenerator.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM verses ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerse(): Verse?

    @Query("SELECT * FROM verses WHERE translation = :translation AND book = :book AND chapter = :chapter ORDER BY verse ASC")
    fun getVersesOrder(translation: String, book: String, chapter: Int): Flow<List<Verse>>

    @Query("SELECT * FROM verses WHERE translation = :translation AND book = :book AND chapter = :chapter ORDER BY RANDOM()")
    fun getVersesRandom(translation: String, book: String, chapter: Int): Flow<List<Verse>>

    @Query("SELECT DISTINCT book FROM verses ORDER BY book_id ASC")
    fun getAllBooks(): Flow<List<String>>

    @Query("SELECT MAX(chapter) FROM verses WHERE book = :book")
    fun getChapterCount(book: String): Flow<Int>
}