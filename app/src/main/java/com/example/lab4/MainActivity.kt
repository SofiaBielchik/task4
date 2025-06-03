package com.example.lab4

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.lab4.ui.theme.Lab4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MediaPlayerScreen()
                }
            }
        }
    }
}

@Composable
fun MediaPlayerScreen() {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val videoView = remember { android.widget.VideoView(context) }
    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_video}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        // ==== АУДІО ====
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Аудіо-плеєр",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        try {
                            mediaPlayer?.release()
                            val afd = context.resources.openRawResourceFd(R.raw.sample_audio)
                            val player = MediaPlayer().apply {
                                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                prepare()
                                setVolume(1.0f, 1.0f)
                                start()
                            }
                            mediaPlayer = player
                            isPlaying = true
                            Log.d("AUDIO", "Playing audio")
                        } catch (e: Exception) {
                            Log.e("AUDIO", "Error: ${e.message}")
                        }
                    }) {
                        Text("Play")
                    }

                    Button(onClick = {
                        mediaPlayer?.pause()
                        isPlaying = false
                    }, enabled = isPlaying) {
                        Text("Pause")
                    }

                    Button(onClick = {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                        isPlaying = false
                    }, enabled = isPlaying) {
                        Text("Stop")
                    }
                }
            }
        }

        Divider(color = Color.Gray, thickness = 1.dp)

        // ==== ВІДЕО ====
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Відео-плеєр",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                AndroidView(
                    factory = {
                        videoView.apply {
                            setVideoURI(videoUri)
                            setMediaController(MediaController(context).also {
                                it.setAnchorView(this)
                            })
                            requestFocus()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        videoView.start()
                    }) {
                        Text("Play")
                    }

                    Button(onClick = {
                        videoView.pause()
                    }) {
                        Text("Pause")
                    }

                    Button(onClick = {
                        videoView.stopPlayback()
                        videoView.setVideoURI(videoUri)
                    }) {
                        Text("Stop")
                    }
                }
            }
        }
    }
}