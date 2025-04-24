package com.example.newscompose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.newscompose.model.Article
import com.example.newscompose.viewmodel.NewsViewModel
import com.example.newscompose.viewmodel.NewsViewModelFactory
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun NewsScreen(query: String) {
    val viewModel: NewsViewModel = viewModel(factory = NewsViewModelFactory(query))
    val news by viewModel.news.collectAsState()

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        val filteredNews = news.filter { it.urlToImage != null }
        items(filteredNews) { item ->
            NewsCard(item = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun NewsCard(item: Article) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                item.url?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            item.urlToImage?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.description ?: "", style = MaterialTheme.typography.bodySmall)
        }
    }
}
