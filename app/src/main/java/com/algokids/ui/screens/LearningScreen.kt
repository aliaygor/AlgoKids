package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import android.content.Context
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.algokids.game.model.GameCategory
import com.algokids.data.LearningContent
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun LearningScreen(
    category: GameCategory,
    language: AppLanguage,
    isSoundEnabled: Boolean,
    tts: TextToSpeech,
    isTtsReady: Boolean,
    onBack: () -> Unit
) {
    val items = remember(category, language) {
        if (category == GameCategory.ALPHABET) LearningContent.alphabet(language == AppLanguage.TR) else LearningContent.numbers()
    }
    val preferences = LocalContext.current.getSharedPreferences("algokids_progress", Context.MODE_PRIVATE)
    val checkpoint = "learning_${category.name}_${language.name}"
    var index by rememberSaveable(category, language) { mutableIntStateOf(preferences.getInt(checkpoint, 0).coerceIn(0, items.lastIndex)) }
    LaunchedEffect(index) { preferences.edit().putInt(checkpoint, index).apply() }
    if (index > items.lastIndex) index = items.lastIndex
    val item = items[index]
    val title = label(language, item.titleTr, item.titleEn)
    val detail = label(language, item.detailTr, item.detailEn)
    val speakText = label(language, item.speakTr, item.speakEn)

    fun speakCurrent() {
        if (isSoundEnabled && isTtsReady) speak(tts, speakText, language, true)
    }

    LaunchedEffect(isTtsReady, index, language, isSoundEnabled) {
        speakCurrent()
    }
    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFF8E1), Color(0xFFE3F2FD))))
            .safeDrawingPadding().padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).background(Color.White, CircleShape)) {
                    Icon(Icons.Default.Close, label(language, "Menüye dön", "Back to menu"), tint = Color(0xFFD32F2F))
                }
                Text(
                    text = if (category == GameCategory.ALPHABET) label(language, "Alfabe", "Alphabet") else label(language, "Sayılar", "Numbers"),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = Color(0xFF2E7D32)
                )
                IconButton(onClick = { speakCurrent() }, modifier = Modifier.size(44.dp).background(Color.White, CircleShape)) {
                    Icon(Icons.Default.VolumeUp, label(language, "Tekrar dinle", "Listen again"), tint = Color(0xFF1976D2))
                }
            }

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(190.dp)
                    .shadow(10.dp, RoundedCornerShape(38.dp))
                    .background(Color.White, RoundedCornerShape(38.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.symbol, fontSize = 82.sp, fontWeight = FontWeight.Black, color = Color(0xFF1976D2))
            }

            Spacer(Modifier.height(24.dp))
            Text(title, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF37474F), textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(detail, fontSize = 20.sp, lineHeight = 28.sp, color = Color(0xFF546E7A), textAlign = TextAlign.Center)

            if (!isSoundEnabled || !isTtsReady || !configureVoice(tts,language)) {
                Text(label(language, "Ses kapalı veya bu dilin ses paketi hazır değil. Okunuşu karttan takip edebilirsin.", "Sound is off or this language voice is unavailable. Follow the written pronunciation."), modifier = Modifier.padding(top = 12.dp), textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { if (index > 0) index-- },
                    enabled = index > 0,
                    modifier = Modifier.weight(1f).height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, null)
                    Spacer(Modifier.width(6.dp))
                    Text(label(language, "Geri", "Back"))
                }
                Button(
                    onClick = { if (index < items.lastIndex) index++ },
                    enabled = index < items.lastIndex,
                    modifier = Modifier.weight(1f).height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text(label(language, "İleri", "Next"))
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("${index + 1} / ${items.size}", color = Color(0xFF546E7A), fontWeight = FontWeight.Bold)
        }
    }
}
