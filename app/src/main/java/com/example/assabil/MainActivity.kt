package com.example.assabil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.assabil.ui.navigation.AsSabilBottomBar
import com.assabil.ui.navigation.AsSabilNavHost
import com.assabil.ui.theme.AsSabilTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsSabilTheme {
                AsSabilApp()
            }
        }
    }
}

@Composable
fun AsSabilApp() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = androidx.compose.ui.graphics.Color.Transparent,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            color = androidx.compose.ui.graphics.Color.Transparent,
            darkIcons = true
        )
    }

    Scaffold(
        bottomBar = {
            AsSabilBottomBar(navController = navController)
        },
        containerColor = com.assabil.ui.theme.AsSabilColors.Cream
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            AsSabilNavHost(navController = navController)
        }
    }
}
