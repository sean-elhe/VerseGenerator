package com.example.versegenerator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.versegenerator.data.BibleDao

@Database(entities = [Verse::class, FavoriteVerse::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun bibleDao(): BibleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bible_database"
                )
                    .createFromAsset("databases/bibles.db") // Verify this path!
                    .fallbackToDestructiveMigration() // Useful during development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
