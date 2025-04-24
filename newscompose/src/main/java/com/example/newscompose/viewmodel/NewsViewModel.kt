package com.example.newscompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newscompose.model.Article
import com.example.newscompose.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val query: String) : ViewModel() {
    private val _news = MutableStateFlow<List<Article>>(emptyList())
    val news: StateFlow<List<Article>> = _news

    private val apiKey = "07c2ae765f2f451a9cecbd8427cd5fdf"

    init {
        fetchNews()
    }

    private fun fetchNews() {
        viewModelScope.launch {
            try {
                println("Fetch start")
                val result = ApiClient.retrofit.getNewsByKeyword(query, apiKey)
                println("Fetched news: $result")
                _news.value = result.articles
            } catch (e: Exception) {
                e.printStackTrace()
                _news.value = emptyList()
            }
        }
    }
}

class NewsViewModelFactory(private val category: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
