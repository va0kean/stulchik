package com.va0kean.stulchik

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    articles: List<Article>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSortColumnChanged: (String) -> Unit,
    sortColumn: String,
    onStatusUpdate: (Article) -> Unit,
    getFileContent: suspend (String) -> String
) {
    var selectedArticleContent by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск...") }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Статус", modifier = Modifier.weight(0.15f), fontWeight = FontWeight.Bold)
                HeaderCell("Категория", Modifier.weight(0.25f), sortColumn) { onSortColumnChanged(it) }
                HeaderCell("Автор", Modifier.weight(0.25f), sortColumn) { onSortColumnChanged(it) }
                HeaderCell("Название", Modifier.weight(0.35f), sortColumn) { onSortColumnChanged(it) }
            }
            HorizontalDivider()

            LazyColumn {
                items(articles) { article ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    selectedArticleContent = getFileContent(article.fileName)
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status Icon
                        Box(
                            modifier = Modifier
                                .weight(0.15f)
                                .clickable { onStatusUpdate(article) },
                            contentAlignment = Alignment.Center
                        ) {
                            StatusIcon(article.status)
                        }
                        Text(article.category, modifier = Modifier.weight(0.25f))
                        Text(article.author, modifier = Modifier.weight(0.25f))
                        Text(article.title, modifier = Modifier.weight(0.35f))
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    if (selectedArticleContent != null) {
        AlertDialog(
            onDismissRequest = { selectedArticleContent = null },
            confirmButton = {
                TextButton(onClick = { selectedArticleContent = null }) {
                    Text("Закрыть")
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                    LazyColumn {
                        item {
                            Text(text = selectedArticleContent ?: "")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun HeaderCell(text: String, modifier: Modifier, currentSort: String, onSort: (String) -> Unit) {
    Text(
        text = text,
        modifier = modifier.clickable { onSort(text) },
        fontWeight = FontWeight.Bold,
        color = if (currentSort == text) MaterialTheme.colorScheme.primary else Color.Unspecified
    )
}

@Composable
fun StatusIcon(status: Int) {
    when (status) {
        0 -> Icon(Icons.Outlined.Circle, contentDescription = "None")
        1 -> Icon(Icons.Filled.Star, contentDescription = "Priority", tint = Color.Yellow)
        2 -> Icon(Icons.Filled.CheckCircle, contentDescription = "Done", tint = Color.Green)
    }
}
