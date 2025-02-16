package com.example.audiobookplayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen(bookId: String) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(0f) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Now Playing: Book $bookId", style = MaterialTheme.typography.headlineSmall)
        
        LinearProgressIndicator(
            progress = currentTime / 100,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { /* Previous */ }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
            }
            
            IconButton(onClick = { isPlaying = !isPlaying }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            
            IconButton(onClick = { /* Next */ }) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next")
            }
        }
    }
}
