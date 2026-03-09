package com.va0kean.stulchik

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val category: String,
    val author: String,
    val title: String,
    val status: Int = 0 // 0 - none, 1 - priority, 2 - done
)
