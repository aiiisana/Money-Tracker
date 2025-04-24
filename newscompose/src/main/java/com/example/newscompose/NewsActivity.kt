package com.example.newscompose

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.example.newscompose.ui.NewsNavHost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
class NewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("News") },
                        navigationIcon = {
                            IconButton(onClick = {
                                (context as? Activity)?.finish()
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == "general",
                            onClick = {
                                navController.navigate("general") {
                                    launchSingleTop = true
                                }
                            },
                            label = { Text("Economy") },
                            icon = { Icon(Icons.Default.List, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "forex",
                            onClick = {
                                navController.navigate("forex") {
                                    launchSingleTop = true
                                }
                            },
                            label = { Text("Forex") },
                            icon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "crypto",
                            onClick = {
                                navController.navigate("crypto") {
                                    launchSingleTop = true
                                }
                            },
                            label = { Text("Crypto") },
                            icon = { Icon(Icons.Default.Star, contentDescription = null) }
                        )
                    }
                }
            ) { paddingValues ->
                NewsNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
