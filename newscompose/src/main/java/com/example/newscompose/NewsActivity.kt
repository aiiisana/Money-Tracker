package com.example.newscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.example.newscompose.ui.NewsNavHost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class NewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
//          val context = LocalContext.current
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                containerColor = Color.Black,
//                topBar = {
//                    TopAppBar(
//                        title = {
//                            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart){
//                                Text("News", color = Color.White)
//                            }
//                        },
//                        navigationIcon = {
//                            IconButton(onClick = {
//                                (context as? Activity)?.finish()
//                            }) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
//                            }
//                        },
//                        colors = TopAppBarDefaults.topAppBarColors(
//                            containerColor = Color.Black
//                        ),
//                        modifier = Modifier.height(48.dp)
//                    )
//                },
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier.height(56.dp),
                        containerColor = Color(0xFF0F1115)
                    ) {
                        val selectedColor = Color(0xFF6A66FF)
                        val unselectedColor = Color.White

                        @Composable
                        fun NavItem(
                            route: String,
                            icon: ImageVector,
                            label: String
                        ) {
                            val selected = currentRoute == route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                },
                                icon = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (selected) selectedColor else unselectedColor
                                        )
                                        if (selected) {
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text(
                                                text = label,
                                                color = selectedColor,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                },
                                alwaysShowLabel = false,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent,
                                    selectedIconColor = selectedColor,
                                    selectedTextColor = selectedColor,
                                    unselectedIconColor = unselectedColor,
                                    unselectedTextColor = unselectedColor,
                                )
                            )
                        }

                        NavItem("general", Icons.Default.List, "Economy")
                        NavItem("forex", Icons.Default.AttachMoney, "Forex")
                        NavItem("crypto", Icons.Default.Star, "Crypto")
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
