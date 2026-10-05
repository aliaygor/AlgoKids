package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.engine.*
import kotlinx.coroutines.delay

@Composable
fun AlgorithmGameScreen(
    language: AppLanguage, isSoundEnabled: Boolean, tts: TextToSpeech, isTtsReady: Boolean,
    onToggleSound: () -> Unit, onBack: () -> Unit
) {
    val prefs = LocalContext.current.getSharedPreferences("algokids_progress", Context.MODE_PRIVATE)
    val levels = RouteLevels.all
    var levelIndex by rememberSaveable { mutableIntStateOf(prefs.getInt("route_level", 0).coerceIn(0, levels.lastIndex)) }
    val level = levels[levelIndex]
    var robot by remember(levelIndex) { mutableStateOf(level.start) }
    var message by remember(levelIndex) { mutableStateOf("") }
    var complete by remember(levelIndex) { mutableStateOf(false) }
    var running by remember { mutableStateOf(false) }
    var runId by remember { mutableIntStateOf(0) }
    var activeStep by remember(levelIndex) { mutableIntStateOf(0) }
    var finishedAll by remember { mutableStateOf(false) }
    val commands = remember(levelIndex) { mutableStateListOf<Move>() }
    val visited = remember(levelIndex) { mutableStateListOf<GridPoint>() }
    var attempts by remember(levelIndex) { mutableIntStateOf(0) }

    BackHandler { onBack() }
    DisposableEffect(Unit) { onDispose { tts.stop() } }
    LaunchedEffect(levelIndex) { prefs.edit().putInt("route_level", levelIndex).apply() }
    LaunchedEffect(levelIndex, language, isSoundEnabled, isTtsReady) {
        if (isTtsReady) speak(tts, label(language, "${level.titleTr}. Her ok bir kare ilerletir. Başlangıç karesini adım olarak sayma.", "${level.titleEn}. Each arrow moves one square. The starting square is not a step."), language, isSoundEnabled)
    }
    LaunchedEffect(runId) {
        if (runId == 0) return@LaunchedEffect
        val result = RouteEngine.run(level, commands.toList())
        robot = level.start
        visited.clear()
        activeStep = 0
        result.path.drop(1).forEachIndexed { index, point ->
            delay(450)
            robot = point
            visited.add(point)
            activeStep = index + 1
        }
        if (result.failedStep != null) {
            activeStep = result.failedStep
            message = label(language, "${result.failedStep}. komut engelle veya tahta kenarıyla karşılaştı. Bu oku değiştir.", "Command ${result.failedStep} hit a wall or the edge. Change that arrow.")
        } else if (result.reachedGoal) {
            complete = true
            prefs.edit().putBoolean("route_done_$levelIndex", true).apply()
            message = label(language, "Başardın! ${commands.size} komutla en kısa yolu kurdun.", "You did it! You used the shortest path: ${commands.size} commands.")
        } else {
            message = label(language, "${result.path.size - 1} adım attın. Hedefe ulaşmak için okların sırasını kontrol et.", "You moved ${result.path.size - 1} steps. Check the arrow order to reach the goal.")
        }
        running = false
        if (isTtsReady) speak(tts, message, language, isSoundEnabled)
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF4F6FC)).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text(label(language, "‹ Oyunlar", "‹ Games")) }
            Text(label(language, "Rota atölyesi", "Route workshop"), Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFF172B4D))
            TextButton(onClick = onToggleSound) { Text(if (isSoundEnabled) label(language, "Sesi kapat", "Mute") else label(language, "Sesi aç", "Sound on")) }
        }
        LinearProgressIndicator(progress = { (levelIndex + 1f) / levels.size }, modifier = Modifier.fillMaxWidth())
        Text(label(language, "Bölüm ${levelIndex + 1} / ${levels.size} · Planlama", "Level ${levelIndex + 1} / ${levels.size} · Planning"), style = MaterialTheme.typography.labelLarge)
        Text(label(language, level.titleTr, level.titleEn), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label(language, "Her ok = 1 kare. Başlangıç karesi adım değildir. Engelleri aşmadan ${level.maxCommands} adımda hedefe ulaş.", "Each arrow = 1 square. The starting square is not a step. Reach the goal in ${level.maxCommands} steps without hitting a wall."), style = MaterialTheme.typography.bodyLarge)
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.widthIn(max = 440.dp).fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(level.size) { y ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(level.size) { x ->
                            val point = GridPoint(x, y)
                            val blocked = point in level.blocks
                            Box(Modifier.weight(1f).aspectRatio(1f).background(when { blocked -> Color(0xFF64748B); point == level.goal -> Color(0xFFFFE9AD); point in visited -> Color(0xFFD7F4EA); else -> Color(0xFFEDF1FA) }, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                                Text(when { point == robot -> level.hero; point == level.goal -> level.goalEmoji; blocked -> "▧"; point == level.start -> "○"; else -> "" }, fontSize = 28.sp)
                            }
                        }
                    }
                }
            }
        }
        Text(label(language, "Programın · ${commands.size}/${level.maxCommands} komut · Çalışan adım: $activeStep", "Your program · ${commands.size}/${level.maxCommands} commands · Current step: $activeStep"), fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(level.maxCommands) { index ->
                Surface(shape = RoundedCornerShape(10.dp), color = if (activeStep == index + 1) Color(0xFFD7F4EA) else Color.White, modifier = Modifier.size(48.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${index + 1}", fontSize = 10.sp)
                        Text(commands.getOrNull(index)?.icon ?: "·", fontSize = 22.sp)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Move.entries.forEach { move ->
                FilledTonalButton(onClick = { commands.add(move); message = "" }, enabled = !running && !complete && commands.size < level.maxCommands, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(56.dp), shape = RoundedCornerShape(16.dp)) { Text(move.icon, fontSize = 28.sp) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = { commands.removeAt(commands.lastIndex); robot = level.start; visited.clear(); activeStep = 0; message = "" }, enabled = !running && !complete && commands.isNotEmpty()) { Text(label(language, "Son oku sil", "Undo")) }
            TextButton(onClick = { commands.clear(); robot = level.start; visited.clear(); complete = false; message = ""; activeStep = 0 }, enabled = !running) { Text(label(language, "Sıfırla", "Reset")) }
            TextButton(onClick = {
                val solution = level.solution
                val prefix = commands.toList() == solution.take(commands.size)
                message = if (prefix && commands.size < solution.size) label(language, "Bir sonraki adım için ${solution[commands.size].icon} yönünü düşün.", "Think about ${solution[commands.size].icon} for the next step.") else label(language, "Hedefe gitmeden önce engellerin çevresindeki boş kareleri incele. Son oklarını geri almayı dene.", "Look at the empty squares around the walls. Try undoing your last arrows.")
            }, enabled = !running && !complete) { Text(label(language, "İpucu", "Hint")) }
        }
        if (message.isNotBlank()) Surface(color = Color(0xFFE6ECFA), shape = RoundedCornerShape(16.dp)) { Text(message, Modifier.padding(16.dp)) }
        Button(onClick = {
            if (complete) {
                if (levelIndex < levels.lastIndex) levelIndex++ else finishedAll = true
            } else { running = true; attempts++; message = ""; runId++ }
        }, enabled = !running && (complete || commands.isNotEmpty()), modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(16.dp)) {
            Text(if (running) label(language, "Adımları izle…", "Watch the steps…") else if (complete) label(language, "${if (levelIndex == levels.lastIndex) "Atölyeyi bitir" else "Sonraki bölüm"}", if (levelIndex == levels.lastIndex) "Finish workshop" else "Next level") else label(language, "Programı çalıştır", "Run program"))
        }
        Text(label(language, "Deneme: $attempts · Önce planla, sonra sonucu gözle ve düzelt.", "Attempts: $attempts · Plan, observe the result, then improve."), style = MaterialTheme.typography.bodySmall)
    }
    if (finishedAll) AlertDialog(onDismissRequest = { finishedAll = false }, title = { Text(label(language, "Rota atölyesi tamamlandı!", "Route workshop complete!")) }, text = { Text(label(language, "Şimdi algoritma kategorisindeki döngü ve hata ayıklama görevlerini dene.", "Try the loop and debugging missions in the algorithm category next.")) }, confirmButton = { TextButton(onClick = onBack) { Text(label(language, "Oyunlara dön", "Back to games")) } }, dismissButton = { TextButton(onClick = { levelIndex = 0; finishedAll = false }) { Text(label(language, "Tekrar oyna", "Play again")) } })
}
