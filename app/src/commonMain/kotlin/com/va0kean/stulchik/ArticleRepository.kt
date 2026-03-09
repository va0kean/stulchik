package com.va0kean.stulchik

import kotlinx.coroutines.flow.Flow
import java.nio.charset.Charset

class ArticleRepository(private val articleDao: ArticleDao) {

    val allArticles: Flow<List<Article>> = articleDao.getAllArticles()

    suspend fun updateStatus(fileName: String, newStatus: Int) {
        articleDao.updateStatusByFileName(fileName, newStatus)
    }

    suspend fun insertArticles(articles: List<Article>) {
        articleDao.insertArticles(articles)
    }
}
