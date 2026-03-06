package com.va0kean.stulchik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortColumn = MutableStateFlow("fileName")
    val sortColumn = _sortColumn.asStateFlow()

    private val _indexingProgress = MutableStateFlow<Float?>(null)
    val indexingProgress = _indexingProgress.asStateFlow()

    val articles: StateFlow<List<Article>> = combine(
        repository.allArticles,
        _searchQuery,
        _sortColumn
    ) { articles, query, sortCol ->
        val filtered = articles.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.author.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true)
        }
        when (sortCol) {
            "Категория" -> filtered.sortedBy { it.category }
            "Автор" -> filtered.sortedBy { it.author }
            "Название" -> filtered.sortedBy { it.title }
            else -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.indexAssetsIfNeeded { progress ->
                _indexingProgress.value = if (progress < 1.0f) progress else null
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortColumnChanged(column: String) {
        _sortColumn.value = column
    }

    fun updateStatus(article: Article) {
        viewModelScope.launch {
            val nextStatus = (article.status + 1) % 3
            repository.updateStatus(article.fileName, nextStatus)
        }
    }
    
    suspend fun getFileContent(fileName: String): String {
        return repository.getFileContent(fileName)
    }
}

class ArticleViewModelFactory(private val repository: ArticleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
