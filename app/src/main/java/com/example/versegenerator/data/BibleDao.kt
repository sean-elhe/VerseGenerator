package com.example.versegenerator.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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

    @Query("SELECT DISTINCT book FROM verses WHERE book LIKE :searchQuery")
    fun searchBooks(searchQuery: String): Flow<List<String>>

    @Query("""
    SELECT DISTINCT chapter FROM verses 
    WHERE book = :book COLLATE NOCASE
    AND (:searchQuery = '' OR CAST(chapter AS TEXT) LIKE :searchQuery || '%')
    ORDER BY chapter ASC
""")
    fun searchChapter(searchQuery: String, book: String): Flow<List<Int>>


    // SAVED

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavorite(favoriteVerse: List<FavoriteVerse>)

    @Delete
    fun removeFavorite(favoriteVerse: FavoriteVerse)

    @Query("SELECT * FROM saved ORDER BY book_id ASC")
    fun getFavorites(): Flow<List<FavoriteVerse>>



}