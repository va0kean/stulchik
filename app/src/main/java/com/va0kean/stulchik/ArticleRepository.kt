package com.va0kean.stulchik

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.nio.charset.Charset

class ArticleRepository(private val articleDao: ArticleDao, private val context: Context) {

    val allArticles: Flow<List<Article>> = articleDao.getAllArticles()

    suspend fun updateStatus(fileName: String, newStatus: Int) {
        articleDao.updateStatusByFileName(fileName, newStatus)
    }

    suspend fun indexAssetsIfNeeded(onProgress: (Float) -> Unit) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("indexed", false)) return

        withContext(Dispatchers.IO) {
            val assetManager = context.assets
            val files = assetManager.list("articles") ?: return@withContext
            val total = files.size
            val batchSize = 100
            val articlesToInsert = mutableListOf<Article>()

            files.forEachIndexed { index, fileName ->
                try {
                    val inputStream = assetManager.open("articles/$fileName")
                    val reader = inputStream.bufferedReader(Charset.forName("Windows-1251"))
                    val lines = reader.readLines()
                    inputStream.close()

                    if (lines.size >= 3) {
                        val categoryLine = lines[0].removePrefix("Категория:").trim()
                        val authorLine = lines[1].removePrefix("Автор:").trim()
                        val titleLine = lines[2].removePrefix("Название:").trim()

                        val categories = categoryLine.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        
                        categories.forEach { cat ->
                            articlesToInsert.add(
                                Article(
                                    fileName = fileName,
                                    category = cat,
                                    author = authorLine,
                                    title = titleLine
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (articlesToInsert.size >= batchSize || index == total - 1) {
                    articleDao.insertArticles(articlesToInsert)
                    articlesToInsert.clear()
                }
                
                onProgress((index + 1).toFloat() / total)
            }
            prefs.edit().putBoolean("indexed", true).apply()
        }
    }
    
    suspend fun getFileContent(fileName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                context.assets.open("articles/$fileName").use { input ->
                    input.bufferedReader(Charset.forName("Windows-1251")).readText()
                }
            } catch (e: Exception) {
                "Error reading file: ${e.message}"
            }
        }
    }
}
