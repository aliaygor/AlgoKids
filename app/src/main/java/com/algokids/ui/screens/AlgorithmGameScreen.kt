package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

private enum class Move(val icon: String, val dx: Int, val dy: Int) {
    UP("↑", 0, -1),
    DOWN("↓", 0, 1),
    LEFT("←", -1, 0),
    RIGHT("→", 1, 0)
}

private data class GridPoint(val x: Int, val y: Int)

private data class AlgorithmLevel(
    val title: String,
    val helper: String,
    val hero: String,
    val goalEmoji: String,
    val blockEmoji: String,
    val start: GridPoint,
    val goal: GridPoint,
    val blocks: Set<GridPoint>,
    val maxCommands: Int,
    val hint: List<Move>
)

@Composable
fun AlgorithmGameScreen(
    language: AppLanguage,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
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

    val levels = remember {
        listOf(
            AlgorithmLevel("Roket yıldıza gitsin", "Oklara bas, yolu hazırla.", "🚀", "⭐", "☄️", GridPoint(0, 2), GridPoint(3, 0), setOf(GridPoint(1, 1)), 5, listOf(Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.UP, Move.UP)),
            AlgorithmLevel("Tavşan havuca gitsin", "Engellere çarpma.", "🐰", "🥕", "🪨", GridPoint(0, 0), GridPoint(3, 3), setOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 2)), 6, listOf(Move.DOWN, Move.DOWN, Move.RIGHT, Move.DOWN, Move.RIGHT, Move.RIGHT)),
            AlgorithmLevel("Robot şarja gitsin", "Önce sola, sonra aşağı.", "🤖", "🔋", "🧱", GridPoint(3, 0), GridPoint(0, 3), setOf(GridPoint(2, 1), GridPoint(1, 1)), 6, listOf(Move.LEFT, Move.LEFT, Move.LEFT, Move.DOWN, Move.DOWN, Move.DOWN)),
            AlgorithmLevel("Kargo eve gitsin", "Kısa yolu bul.", "📦", "🏠", "🌳", GridPoint(0, 3), GridPoint(3, 1), setOf(GridPoint(1, 2), GridPoint(2, 2)), 5, listOf(Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.UP, Move.UP)),
            AlgorithmLevel("Arı çiçeğe gitsin", "Çiçeğe kadar uç.", "🐝", "🌸", "🌧️", GridPoint(0, 1), GridPoint(3, 2), setOf(GridPoint(1, 2), GridPoint(2, 1)), 5, listOf(Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.DOWN)),
            AlgorithmLevel("Balık denize gitsin", "Taşlardan uzak dur.", "🐟", "🌊", "🪨", GridPoint(3, 3), GridPoint(0, 0), setOf(GridPoint(2, 2), GridPoint(1, 2), GridPoint(2, 0)), 6, listOf(Move.LEFT, Move.LEFT, Move.DOWN, Move.LEFT, Move.UP, Move.UP)),
            AlgorithmLevel("Tren istasyona gitsin", "Ray gibi sırala.", "🚂", "🚉", "🚧", GridPoint(0, 0), GridPoint(3, 2), setOf(GridPoint(0, 1), GridPoint(2, 1)), 5, listOf(Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.DOWN, Move.DOWN)),
            AlgorithmLevel("Kedi eve dönsün", "Adımları sıraya koy.", "🐱", "🏠", "🌧️", GridPoint(3, 1), GridPoint(0, 3), setOf(GridPoint(2, 2), GridPoint(1, 1)), 5, listOf(Move.LEFT, Move.LEFT, Move.LEFT, Move.DOWN, Move.DOWN))
        )
    }

    var levelIndex by remember { mutableIntStateOf(0) }
    var robot by remember { mutableStateOf(levels.first().start) }
    var message by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    val commands = remember { mutableStateListOf<Move>() }
    val level = levels[levelIndex]
    val isLevelComplete = robot == level.goal

    fun speakShort(text: String) {
        if (isSoundEnabled && isTtsReady) speak(tts, text, language, true)
    }

    fun speakLevelGuide(force: Boolean = false) {
        if (!isSoundEnabled && !force) return
        val guide = label(
            language,
            "${level.title}. Okları diz, başlat.",
            "Make a path. Add arrows. Then run."
        )
        if (isTtsReady) speak(tts, guide, language, true)
    }

    fun resetLevel() {
        robot = level.start
        commands.clear()
    }

    fun loadLevel(next: Int) {
        levelIndex = next
        val newLevel = levels[next]
        robot = newLevel.start
        commands.clear()
        message = ""
    }

    fun runProgram() {
        var current = level.start
        var crashed = false
        commands.forEach { move ->
            val next = GridPoint(current.x + move.dx, current.y + move.dy)
            if (next.x !in 0..3 || next.y !in 0..3 || next in level.blocks) {
                crashed = true
            } else {
                current = next
            }
        }

        robot = current
        if (!crashed && current == level.goal) {
            score++
            message = label(language, "Aferin, başardın!", "Great, you did it!")
            speakShort(label(language, "Aferin, başardın!", "Great, you did it!"))
        } else {
            misses++
            message = label(language, "Bir daha deneyelim.", "Try again.")
            speakShort(label(language, "Bir daha deneyelim.", "Try again."))
        }
    }

    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D47A1), Color(0xFF4DD0E1), Color(0xFFFFF59D))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).background(Color.White, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF0D47A1))
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).background(Color(0xFFFFEBEE), CircleShape)) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFFD32F2F))
                }
                Text(
                    text = label(language, "Algoritma", "Algorithm"),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                IconButton(
                    onClick = {
                        if (isSoundEnabled) {
                            speakLevelGuide(force = true)
                        } else {
                            onToggleSound()
                            val guide = label(
                                language,
                                "Ses açık. ${level.title}. Okları diz, başlat.",
                                "Sound on. Make a path. Add arrows. Then run."
                            )
                            if (isTtsReady) speak(tts, guide, language, true)
                        }
                    },
                    modifier = Modifier.size(44.dp).background(Color.White, CircleShape)
                ) {
                    Icon(if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff, null, tint = Color(0xFF0D47A1))
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "${levelIndex + 1}/${levels.size}   ${label(language, "Puan", "Score")} $score   ${label(language, "Hata", "Miss")} $misses",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(level.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(level.helper, color = Color.White.copy(alpha = 0.92f), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(level.hero, fontSize = 28.sp)
                Text("→", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1))
                Text(level.goalEmoji, fontSize = 28.sp)
            }

            Spacer(Modifier.height(10.dp))

            AlgorithmBoard(level = level, robot = robot)

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(level.maxCommands) { index ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                            .border(2.dp, Color(0xFF90CAF9), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(commands.getOrNull(index), label = "") { move ->
                            Text(move?.icon ?: "·", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1))
                        }
                    }
                }
            }

            if (message.isNotBlank()) {
                Text(message, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Move.values().forEach { move ->
                    Button(
                        onClick = {
                            if (commands.size < level.maxCommands) {
                                commands.add(move)
                                message = ""
                            }
                        },
                        modifier = Modifier.size(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(move.icon, fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { if (commands.isNotEmpty()) commands.removeAt(commands.lastIndex) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFDE7)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(label(language, "Sil", "Del"), color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(
                    onClick = {
                        resetLevel()
                        message = ""
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFDE7)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(label(language, "Sıfırla", "Reset"), color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(
                    onClick = {
                        commands.clear()
                        commands.addAll(level.hint)
                        message = label(language, "Yol hazır.", "Path ready.")
                        speakShort(label(language, "Yol hazır.", "Path ready."))
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFDE7)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(label(language, "İpucu", "Hint"), color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    if (isLevelComplete) {
                        loadLevel((levelIndex + 1) % levels.size)
                    } else {
                        runProgram()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isLevelComplete) label(language, "Sonraki", "Next") else label(language, "Başlat", "Run"),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun AlgorithmBoard(level: AlgorithmLevel, robot: GridPoint) {
    Column(
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(18.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (y in 0..3) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (x in 0..3) {
                    val point = GridPoint(x, y)
                    val isBlock = point in level.blocks
                    val text = when (point) {
                        robot -> "🤖"
                        level.goal -> "⭐"
                        else -> if (isBlock) level.blockEmoji else ""
                    }
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .background(
                                when {
                                    isBlock -> Color(0xFF6D4C41)
                                    point == level.goal -> Color(0xFFFFF59D)
                                    else -> Color(0xFFE3F2FD)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (point) {
                                robot -> level.hero
                                level.goal -> level.goalEmoji
                                else -> text
                            },
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}
