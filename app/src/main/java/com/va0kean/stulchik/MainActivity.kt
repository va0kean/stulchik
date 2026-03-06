package com.va0kean.stulchik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = ArticleRepository(
            AppDatabase.getDatabase(applicationContext).articleDao(),
            applicationContext
        )
        setContent {
            val viewModel: ArticleViewModel = viewModel(
                factory = ArticleViewModelFactory(repository)
            )
            MainScreen(viewModel)
        }
    }
}

@Composable
fun MainScreen(viewModel: ArticleViewModel) {
    val articles by viewModel.articles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val indexingProgress by viewModel.indexingProgress.collectAsState()
    val sortColumn by viewModel.sortColumn.collectAsState()
    
    var selectedArticleContent by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск...") }
                )
                if (indexingProgress != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { indexingProgress!! },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Индексация: ${(indexingProgress!! * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
                HeaderCell("Категория", Modifier.weight(0.25f), sortColumn) { viewModel.onSortColumnChanged(it) }
                HeaderCell("Автор", Modifier.weight(0.25f), sortColumn) { viewModel.onSortColumnChanged(it) }
                HeaderCell("Название", Modifier.weight(0.35f), sortColumn) { viewModel.onSortColumnChanged(it) }
            }
            HorizontalDivider()

            LazyColumn {
                items(articles) { article ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    selectedArticleContent = viewModel.getFileContent(article.fileName)
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status Icon
                        Box(
                            modifier = Modifier
                                .weight(0.15f)
                                .clickable { viewModel.updateStatus(article) },
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
