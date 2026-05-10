package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    language: AppLanguage,
    isSoundEnabled: Boolean,
    tts: TextToSpeech,
    isTtsReady: Boolean,
    onToggleSound: () -> Unit,
    onBack: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    fun storyLabel(tr: String, en: String) = if (language == AppLanguage.TR) tr else en
    val storyTitle = storyLabel(story.title, story.titleEn)
    val storyPages = if (language == AppLanguage.TR) story.pages else story.pagesEn
    var completedUtterance by remember { mutableStateOf<String?>(null) }
    fun configureStoryVoice() {
        tts.language = language.locale
        tts.setSpeechRate(if (language == AppLanguage.TR) 0.78f else 0.84f)
        tts.setPitch(if (language == AppLanguage.TR) 1.14f else 1.1f)
        val bestVoice = tts.voices
            ?.filter { it.locale.language == language.locale.language }
            ?.maxByOrNull { voice ->
                val name = voice.name.lowercase(Locale.ROOT)
                val feminineHint = listOf("female", "woman", "girl", "kadin", "kadın", "fem").any { it in name }
                val naturalHint = listOf("network", "neural", "wavenet", "premium").any { it in name }
                voice.quality * 1000 - voice.latency + (if (feminineHint) 500 else 0) + (if (naturalHint) 250 else 0)
            }
        if (bestVoice != null) {
            tts.voice = bestVoice
        }
    }

    DisposableEffect(tts) {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onError(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) {
                completedUtterance = utteranceId
            }
        })
        onDispose {
            tts.stop()
            tts.setOnUtteranceProgressListener(null)
        }
    }
    LaunchedEffect(isTtsReady, language) {
        if (isTtsReady) {
            configureStoryVoice()
        }
    }

    LaunchedEffect(story.id, currentPage, language, isSoundEnabled, isTtsReady) {
        if (isSoundEnabled && isTtsReady) {
            configureStoryVoice()
            val utteranceId = "story_${story.id}_$currentPage"
            completedUtterance = null
            tts.speak(
                storyPages[currentPage],
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        } else if (!isSoundEnabled) {
            tts.stop()
        }
    }

    LaunchedEffect(completedUtterance) {
        val utterance = completedUtterance ?: return@LaunchedEffect
        if (utterance == "story_${story.id}_$currentPage" && currentPage < storyPages.size - 1) {
            currentPage++
        }
    }

    BackHandler {
        if (currentPage > 0) {
            currentPage--
        } else {
            onBack()
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
                    text = storyTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                IconButton(
                    onClick = {
                        onToggleSound()
                        tts.stop()
                    },
                    modifier = Modifier.background(if (isSoundEnabled) Color.White else Color(0xFFEEEEEE), RoundedCornerShape(12.dp)).shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Icon(if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff, contentDescription = null, tint = if (isSoundEnabled) Color(0xFF1976D2) else Color.Gray)
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
                            text = storyPages[currentPage],
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
                    Text(storyLabel("Geri", "Back"))
                }

                Text("${currentPage + 1} / ${storyPages.size}", fontWeight = FontWeight.Bold)

                Button(
                    onClick = { 
                        if (currentPage < storyPages.size - 1) {
                            currentPage++
                        } else onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                ) {
                    Text(if (currentPage < storyPages.size - 1) storyLabel("İleri", "Next") else storyLabel("Bitti", "Done"))
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }
}
