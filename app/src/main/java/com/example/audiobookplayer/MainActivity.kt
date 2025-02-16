package com.example.audiobookplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.audiobookplayer.ui.LibraryScreen
import com.example.audiobookplayer.ui.PlayerScreen
import com.example.audiobookplayer.ui.AudiobookPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AudiobookPlayerTheme {
                MainContent()
            }
        }

        // Set status bar color to transparent
        window.statusBarColor = 0x00000000
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "library") {
        composable("library") { LibraryScreen(onBookClick = { bookId ->
            navController.navigate("player/$bookId")
        }) }
        composable(
            "player/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            if (bookId != null) {
                PlayerScreen(bookId = bookId)
            }
        }
    }
}
