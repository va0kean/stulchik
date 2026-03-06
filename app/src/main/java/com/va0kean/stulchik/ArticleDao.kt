package com.va0kean.stulchik

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Query("UPDATE articles SET status = :newStatus WHERE fileName = :fileName")
    suspend fun updateStatusByFileName(fileName: String, newStatus: Int)

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getCount(): Int
}
