package com.va0kean.stulchik

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val articles = remember { 
        listOf(
            "Статья 1: Введение в Kotlin Multiplatform",
            "Статья 2: Как создавать PWA",
            "Статья 3: Работа с файлами .txt",
            "Статья 4: Возможности Gemini AI"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stulchik Articles") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(articles) { article ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* Здесь будет логика открытия статьи */ }
                ) {
                    Text(
                        text = article,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
