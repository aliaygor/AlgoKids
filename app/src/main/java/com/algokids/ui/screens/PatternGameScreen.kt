package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.engine.GameEngine
import com.algokids.game.model.GameContent
import com.algokids.ui.components.GameScene
import java.util.Locale

@Composable
fun PatternGameScreen(
    items: List<GameContent>,
    onBack: () -> Unit
) {
    if (items.isEmpty()) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val context = LocalContext.current
    val tts = remember { TextToSpeech(context, null) }
    
    var index by remember { mutableIntStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }
    
    // Oyun durumları
    val currentContent = items.getOrNull(index) ?: items[0]
    var isCorrect by remember(index) { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tts.language = Locale("tr", "TR")
        tts.speak("Bak ve devamını bul!", TextToSpeech.QUEUE_FLUSH, null, null)
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Oyundan Çıkıyor Musun?") },
            text = { Text("Eğer çıkarsan ilerlemen kaybolabilir. Devam etmek istiyor musun?") },
            confirmButton = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Evet, Çık")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Hayır, Devam Et")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    GameScene(
        title = "Örüntü Oyunu",
        instruction = "Bak ve devamını bul!",
        progress = (index + 1).toFloat() / items.size,
        onBack = { if (index > 0) index-- else showExitDialog = true },
        onExit = { showExitDialog = true },
        onSound = {
            tts.speak("Bak ve devamını bul!", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    ) {
        // Kart İçeriği
        Text(
            text = "👀 Bak ve devamını bulun!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Üst Sıra (Örüntü)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val sequence = currentContent.sequence ?: currentContent.questionAssets ?: emptyList()
            sequence.forEach { color ->
                PatternCircle(color)
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alt Sıra (Cevap ve Boşluk)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İlk cevap seçeneği (Görseldeki gibi yerleşim)
            PatternCircle(currentContent.answer ?: "", isVisible = isCorrect)
            Spacer(modifier = Modifier.width(12.dp))
            // Boş/Gri yuvarlak
            PatternCircle("GRAY", isVisible = !isCorrect)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Seçenekler (Görselde altta butonlar var)
        if (!isCorrect) {
            Row {
                val options = currentContent.options ?: emptyList()
                options.forEach { option ->
                    PatternOption(option) {
                        if (option == currentContent.answer) {
                            isCorrect = true
                            tts.speak("Aferin!", TextToSpeech.QUEUE_FLUSH, null, null)
                        } else {
                            tts.speak("Bir daha dene", TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // İleri Butonu
        Button(
            onClick = {
                if (index < items.size - 1) {
                    index++
                } else {
                    onBack() // Oyun bitti
                }
            },
            enabled = isCorrect,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BC34A),
                disabledContainerColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .width(160.dp)
                .height(50.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("İleri", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun PatternCircle(colorName: String, isVisible: Boolean = true) {
    val color = when (colorName) {
        "RED" -> Color.Red
        "BLUE" -> Color.Blue
        "GRAY" -> Color.Gray
        "apple" -> Color.Red // Fallback for JSON items
        "banana" -> Color.Yellow
        else -> Color.LightGray
    }

    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(if (isVisible) color else Color.LightGray.copy(alpha = 0.3f))
    )
}

@Composable
fun PatternOption(colorName: String, onClick: () -> Unit) {
    val color = when (colorName) {
        "RED" -> Color.Red
        "BLUE" -> Color.Blue
        "apple" -> Color.Red
        "banana" -> Color.Yellow
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
    )
}
