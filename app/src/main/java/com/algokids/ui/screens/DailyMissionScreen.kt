package com.algokids.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.algokids.data.DailyMission

@Composable
fun DailyMissionScreen(language: AppLanguage, onBack: () -> Unit) {
    val prefs = LocalContext.current.getSharedPreferences("algokids_progress", 0)
    val age = prefs.getInt("child_age", 0)
    BackHandler(onBack = onBack)
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) { Text(label(language, "‹ Ana sayfa", "‹ Home")) }
        Text(label(language, "Günün keşifleri", "Today’s discoveries"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        if (age !in 4..12) {
            Text(label(language, "Bir yetişkin ebeveyn ayarlarından 4–12 yaş aralığını seçsin. Yaş bu cihazda saklanır ve soru seviyesini seçmek için kullanılır.", "Ask an adult to choose an age from 4–12 in Parent settings. The age stays on this device and is used to choose the question level."))
            return@Column
        }

        // Local calendar date: stable all day and does not depend on network time.
        val day = java.util.Calendar.getInstance().let { "${it.get(java.util.Calendar.YEAR)}-${it.get(java.util.Calendar.DAY_OF_YEAR)}" }
        val seed = day.hashCode().toLong()
        val questions = remember(age, day) { DailyMission.questions(age, seed) }
        val completeKey = "daily_solved_${day}_$age"
        var completed by rememberSaveable(day, age) { mutableStateOf(prefs.getBoolean(completeKey, false)) }
        var step by rememberSaveable(day, age) {
            mutableIntStateOf(if (completed) questions.size else prefs.getInt("daily_step_${day}_$age", 0).coerceIn(0, questions.size))
        }
        val question = questions.getOrNull(step.coerceAtMost(questions.lastIndex))
        var selection by rememberSaveable(day, age, step) { mutableStateOf<Int?>(null) }
        var mistakes by rememberSaveable(day, age, step) {
            mutableIntStateOf(prefs.getInt("daily_attempts_${day}_${age}_$step", 0))
        }
        var showHint by rememberSaveable(day, age, step) { mutableStateOf(false) }
        var showExplanation by rememberSaveable(day, age, step) { mutableStateOf(false) }
        var feedback by rememberSaveable(day, age, step) { mutableStateOf("") }

        Text(label(language, "$age yaş için seçildi · Her gün 3 kısa soru", "Age $age · 3 short questions each day"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LinearProgressIndicator(
            progress = { if (completed) 1f else step.toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth().height(9.dp)
        )

        if (completed || question == null) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(label(language, "Harika keşif! 🌟", "Wonderful exploring! 🌟"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(label(language, "Bugünün üç sorusunu da tamamladın. Yarın yeni sorular seni bekliyor!", "You finished all three questions today. New ones will be waiting tomorrow!"))
                    Text(label(language, "Keşif puanların: ${prefs.getInt("bonus_points", 0)}", "Discovery points: ${prefs.getInt("bonus_points", 0)}"), fontWeight = FontWeight.Bold)
                }
            }
            return@Column
        }

        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(label(language, "SORU ${step + 1} / ${questions.size}", "QUESTION ${step + 1} / ${questions.size}"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(label(language, question.tr, question.en), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                question.options.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { value ->
                            val selected = selection == value
                            OutlinedButton(
                                onClick = { selection = value; feedback = "" },
                                enabled = !completed,
                                modifier = Modifier.weight(1f).heightIn(min = 54.dp).testTag("daily_$value"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) { Text((if (selected) "✓  " else "") + value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                Button(
                    enabled = selection != null,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("daily_check"),
                    onClick = {
                        if (selection == question.answer) {
                            val next = step + 1
                            if (next >= questions.size) {
                                completed = true
                                prefs.edit().putBoolean(completeKey, true).putInt("daily_step_${day}_$age", questions.size).apply()
                                val bonusKey = "daily_bonus_$day"
                                val first = !prefs.getBoolean(bonusKey, false)
                                prefs.edit().putBoolean(bonusKey, true).putInt("bonus_points", prefs.getInt("bonus_points", 0) + if (first) 20 else 0).apply()
                                feedback = label(language, if (first) "Üçünü de çözdün! Günlük keşif bonusu: +20 puan 🎉" else "Üçünü de çözdün! Bugünün bonusu daha önce alınmış.", if (first) "You solved all three! Daily discovery bonus: +20 points 🎉" else "You solved all three! Today’s bonus was already claimed.")
                            } else {
                                step = next
                                prefs.edit().putInt("daily_step_${day}_$age", next).apply()
                                feedback = label(language, "Doğru! Bir sonraki keşfe geçelim ✨", "Correct! Let’s try the next discovery ✨")
                            }
                        } else {
                            mistakes++
                            prefs.edit().putInt("daily_attempts_${day}_${age}_$step", mistakes).apply()
                            showHint = true
                            feedback = label(language, "Güzel deneme! Bir ipucu açtım. Tekrar düşünebilirsin.", "Good try! I’ve opened a hint. Have another think.")
                        }
                    }
                ) { Text(label(language, "Cevabımı kontrol et", "Check my answer")) }

                if (feedback.isNotBlank()) Text(feedback, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.testTag("daily_feedback"))
                if (!showHint && !showExplanation) {
                    TextButton(onClick = { showHint = true }, modifier = Modifier.testTag("daily_hint")) {
                        Text(label(language, "Bir ipucu al", "Get a hint"))
                    }
                }
                if (showHint) {
                    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp)) {
                        Text(label(language, question.hintTr, question.hintEn), Modifier.fillMaxWidth().padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                TextButton(onClick = { showExplanation = !showExplanation }, modifier = Modifier.testTag("daily_explanation")) {
                    Text(if (showExplanation) label(language, "Anlatımı kapat", "Hide explanation") else label(language, "Çözümü adım adım anlat", "Explain it step by step"))
                }
                if (showExplanation) {
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(16.dp)) {
                        Text(label(language, question.whyTr, question.whyEn), Modifier.fillMaxWidth().padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Text(label(language, "Keşif puanların: ${prefs.getInt("bonus_points", 0)} · Puanlar atölye kilitlerini açmaz. İpuçları ve anlatımlar ücretsiz.", "Discovery points: ${prefs.getInt("bonus_points", 0)} · Points do not unlock workshops. Hints and explanations are free."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
