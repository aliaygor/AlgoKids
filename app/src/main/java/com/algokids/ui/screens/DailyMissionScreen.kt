package com.algokids.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.algokids.data.DailyMission

@Composable fun DailyMissionScreen(language:AppLanguage,onBack:()->Unit) {
    val prefs=LocalContext.current.getSharedPreferences("algokids_progress",0)
    val age=prefs.getInt("child_age",0)
    BackHandler(onBack=onBack)
    Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        TextButton(onClick=onBack) { Text(label(language,"‹ Ana sayfa","‹ Home")) }
        Text(label(language,"Günün meydan okuması","Daily challenge"),style=MaterialTheme.typography.headlineMedium)
        if(age !in 4..12) {
            Text(label(language,"Bir yetişkin ebeveyn ayarlarından yaş aralığını seçsin. Yaş yalnızca bu cihazda tutulur; soru zorluğunu seçmek içindir.","Ask an adult to choose an age in parent settings. It stays on this device and only guides question difficulty."))
            return@Column
        }
        // Local calendar date: no 24-hour drift and no network needed.
        val day=java.util.Calendar.getInstance().let { "${it.get(java.util.Calendar.YEAR)}-${it.get(java.util.Calendar.DAY_OF_YEAR)}" }
        val seed=day.hashCode().toLong()
        val question=remember(age,day) { DailyMission.question(age,seed) }
        var selection by rememberSaveable(day,age) { mutableStateOf<Int?>(null) }
        var mistakes by rememberSaveable(day,age) { mutableIntStateOf(prefs.getInt("daily_attempts_$day",0)) }
        var solved by rememberSaveable(day,age) { mutableStateOf(prefs.getBoolean("daily_solved_${day}_$age",false)) }
        var solution by rememberSaveable(day,age) { mutableStateOf(false) }
        var feedback by rememberSaveable(day,age) { mutableStateOf("") }
        Text(label(language,"$age yaş için başlangıç önerisi · Zorluk yaşla yaklaşık uyarlanır.","Starting suggestion for age $age · Difficulty is roughly adjusted by age."))
        Text(label(language,question.tr,question.en),style=MaterialTheme.typography.titleLarge)
        question.options.forEach { value ->
            OutlinedButton(onClick={selection=value},enabled=!solved,modifier=Modifier.fillMaxWidth().testTag("daily_$value")) { Text((if(selection==value) "✓ " else "")+value) }
        }
        Button(enabled=selection!=null && !solved,onClick={
            if(selection==question.answer) {
                solved=true
                val bonusKey="daily_bonus_$day"
                val first=!prefs.getBoolean(bonusKey,false)
                prefs.edit().putBoolean("daily_solved_${day}_$age",true).putBoolean(bonusKey,true).putInt("bonus_points",prefs.getInt("bonus_points",0)+if(first) 20 else 0).apply()
                feedback=label(language,if(first) "Çözdün! Günlük bonus: +20 keşif puanı." else "Çözdün! Bugünün bonusu daha önce alındı.",if(first) "Solved! Daily bonus: +20 discovery points." else "Solved! Today’s bonus has already been claimed.")
            } else {
                mistakes++
                prefs.edit().putInt("daily_attempts_$day",mistakes).apply()
                feedback=label(language,"Henüz olmadı. İşlem sırasını yeniden düşün.","Not yet. Think through the steps again.")
            }
        }) { Text(label(language,"Kontrol et","Check")) }
        if(feedback.isNotEmpty())Text(feedback)
        TextButton(onClick={solution=true}) { Text(label(language,"Çözümün adımlarını öğren","Learn the solution steps")) }
        if(solution || solved)Text(label(language,question.whyTr,question.whyEn))
        Text(label(language,"Keşif puanların: ${prefs.getInt("bonus_points",0)}. Bonus puanlar eğitim setlerinin kilidini açmaz. Açıklamalar ücretsizdir.","Discovery points: ${prefs.getInt("bonus_points",0)}. Bonus points do not unlock learning sets. Explanations are free."),style=MaterialTheme.typography.bodySmall)
    }
}
