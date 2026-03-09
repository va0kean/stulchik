package com.va0kean.stulchik

import androidx.room.*

@Database(entities = [Article::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

// В Multiplatform версии нам нужно использовать RoomDatabase.Builder,
// который работает по-разному в Android и Web.
// Пока оставляем этот файл как основу.
