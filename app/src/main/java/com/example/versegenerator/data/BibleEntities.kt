package com.example.versegenerator.data

import androidx.room.Entity
import androidx.room.Index


@Entity(
    tableName = "verses",
    // This ORDER must match the order in your SQL CREATE TABLE statement exactly
    primaryKeys = ["book_id", "chapter", "verse", "translation"],
    indices = [
        Index(value = ["book_id", "chapter"], name = "idx_context"),
        Index(value = ["translation"], name = "idx_translation"),
        Index(value = ["book"], name = "index_verses_book")
    ]
)
data class Verse(
    val book_id: Int,    // INTEGER + NOT NULL = Int
    val book: String,    // TEXT + NOT NULL = String
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: String
)

@Entity(
    tableName = "saved",
    primaryKeys = ["book_id", "chapter", "verse", "translation"] // This matches your primaryKeyPositions 1, 2, and 3
)
data class FavoriteVerse(
    val book: String,
    val book_id: Int,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: String,
    val savedAt: Long = System.currentTimeMillis(),
)


