package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.model.Story
import java.util.Locale

@Composable
fun StoryScreen(
    story: Story,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tts = remember { TextToSpeech(context, null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var isReading by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF9C4), Color(0xFFF1F8E9))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFD32F2F))
                }

                Text(
                    text = story.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                IconButton(
                    onClick = {
                        isReading = !isReading
                        if (isReading) {
                            tts.setLanguage(Locale("tr"))
                            tts.setSpeechRate(0.7f) // Yavaş okuma
                            tts.speak(story.pages[currentPage], TextToSpeech.QUEUE_FLUSH, null, null)
                        } else {
                            tts.stop()
                        }
                    },
                    modifier = Modifier.background(if (isReading) Color(0xFFE8F5E9) else Color.White, RoundedCornerShape(12.dp)).shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Icon(if (isReading) Icons.Default.Close else Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1976D2))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Story Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f)
                    .padding(bottom = 32.dp)
                    .shadow(8.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image Section
                    val currentEmoji = story.pageImages?.getOrNull(currentPage)
                    if (currentEmoji != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color(0xFFF1F8E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currentEmoji, fontSize = 120.sp)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = story.pages[currentPage],
                            fontSize = 22.sp,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            // Pagination
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                ) {
                    Icon(Icons.Default.ArrowBack, null)
                    Text("Geri")
                }

                Text("${currentPage + 1} / ${story.pages.size}", fontWeight = FontWeight.Bold)

                Button(
                    onClick = { 
                        if (currentPage < story.pages.size - 1) currentPage++
                        else onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                ) {
                    Text(if (currentPage < story.pages.size - 1) "İleri" else "Bitti")
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }
}
