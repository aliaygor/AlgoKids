package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
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

private data class LearningItem(
    val symbol: String,
    val titleTr: String,
    val titleEn: String,
    val detailTr: String,
    val detailEn: String,
    val speakTr: String,
    val speakEn: String
)

@Composable
fun LearningScreen(
    category: GameCategory,
    language: AppLanguage,
    isSoundEnabled: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isTtsReady by remember { mutableStateOf(false) }
    val tts = remember { TextToSpeech(context) { status -> isTtsReady = status == TextToSpeech.SUCCESS } }
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val items = remember(category) {
        if (category == GameCategory.ALPHABET) alphabetItems() else numberItems()
    }
    var index by remember { mutableIntStateOf(0) }
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
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).background(Color.White, CircleShape)) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFFD32F2F))
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
                    Icon(Icons.Default.VolumeUp, null, tint = Color(0xFF1976D2))
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

            Spacer(Modifier.weight(1f))

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

private fun alphabetItems(): List<LearningItem> {
    val letters = listOf(
        "A" to "a", "B" to "be", "C" to "ce", "Ç" to "çe", "D" to "de", "E" to "e",
        "F" to "fe", "G" to "ge", "Ğ" to "yumuşak ge", "H" to "he", "I" to "ı", "İ" to "i",
        "J" to "je", "K" to "ke", "L" to "le", "M" to "me", "N" to "ne", "O" to "o",
        "Ö" to "ö", "P" to "pe", "R" to "re", "S" to "se", "Ş" to "şe", "T" to "te",
        "U" to "u", "Ü" to "ü", "V" to "ve", "Y" to "ye", "Z" to "ze"
    ).map { (letter, sound) ->
        LearningItem(letter, "$letter harfi", "Letter $letter", "Okunuşu: $sound", "Sound: $letter", "$letter. $sound.", "Letter $letter.")
    }
    val syllables = listOf("BA", "BE", "BO", "BU", "MA", "ME", "MO", "MU", "LA", "LE", "SA", "SE").map {
        LearningItem(it, "$it hecesi", "$it syllable", "Birlikte oku: $it", "Read together: $it", "$it.", "$it.")
    }
    return letters + syllables
}

private fun numberItems(): List<LearningItem> = (1..100).map { number ->
    val tr = numberToTurkish(number)
    LearningItem(number.toString(), "$number sayısı", "Number $number", "Okunuşu: $tr", "Count: $number", "$number. $tr.", "Number $number.")
}

private fun numberToTurkish(number: Int): String {
    val ones = listOf("", "bir", "iki", "üç", "dört", "beş", "altı", "yedi", "sekiz", "dokuz")
    val tens = listOf("", "on", "yirmi", "otuz", "kırk", "elli", "altmış", "yetmiş", "seksen", "doksan")
    if (number == 100) return "yüz"
    if (number < 10) return ones[number]
    val ten = number / 10
    val one = number % 10
    return listOf(tens[ten], ones[one]).filter { it.isNotBlank() }.joinToString(" ")
}
