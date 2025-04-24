package com.example.newscompose.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier

@Composable
fun NewsNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "general", modifier = modifier) {
        composable("general") {
            NewsScreen(query = "general")
        }
        composable("forex") {
            NewsScreen(query = "stock market")
        }
        composable("crypto") {
            NewsScreen(query = "crypto")
        }
    }
}
