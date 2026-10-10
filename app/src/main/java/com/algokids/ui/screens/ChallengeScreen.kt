package com.algokids.ui.screens

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.model.*
import com.algokids.data.Curriculum
import com.algokids.data.LearningPath

fun categoryTitle(category: GameCategory, language: AppLanguage) = when (category) {
    GameCategory.VISUAL -> label(language, "Görsel Algı", "Visual Skills")
    GameCategory.NUMERICAL -> label(language, "Sayısal Mantık", "Number Logic")
    GameCategory.LOGIC -> label(language, "Mantık Yürütme", "Reasoning")
    GameCategory.ATTENTION -> label(language, "Dikkat ve Odak", "Attention")
    GameCategory.MEMORY -> label(language, "Hafıza", "Memory")
    GameCategory.AUDIOLOGY -> label(language, "Dinleme ve Anlama", "Listening and Understanding")
    GameCategory.GEOMETRY -> label(language, "Geometri", "Geometry")
    GameCategory.ALPHABET -> label(language, "Alfabe", "Alphabet")
    GameCategory.NUMBERS -> label(language, "Sayılar", "Numbers")
    GameCategory.ALGORITHM -> label(language, "Algoritma", "Algorithm")
}

@Composable
fun CategoryScreen(category: GameCategory, language: AppLanguage, onClassic: () -> Unit, onChallenge: (Challenge) -> Unit, onBack: () -> Unit) {
    val prefs = LocalContext.current.getSharedPreferences("algokids_progress", Context.MODE_PRIVATE)
    BackHandler(onBack = onBack)
    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TextButton(onClick = onBack) { Text(label(language, "‹ Ana sayfa", "‹ Home")) } }
        item { Text(categoryTitle(category, language), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold) }
        item {
            val solved=LearningPath.solved(prefs,category,language.name)
            val total=LearningPath.total(category)
            val completion=LearningPath.progress(prefs,category,language.name,com.algokids.data.ProgressStore(LocalContext.current).foundationCompleted(category,language.name))
            LinearProgressIndicator(progress={completion/100f},modifier=Modifier.fillMaxWidth().height(10.dp))
            Text(label(language,"Eğitim seti: $solved/$total görev · Tamamlanan: $completion/100","Learning set: $solved/$total missions · Completed: $completion/100"))
            Text(label(language,"Bütün puanlı atölyeleri tamamlayınca sonraki set açılır. Her yanlış kontrol, o görevin ilk değerlendirme puanından 25 düşürür (en az 25). Temel alıştırmalar serbest pratik içindir.","Finish every scored workshop to unlock the next set. Each incorrect check reduces that mission’s first assessment score by 25 (minimum 25). Foundation exercises are optional practice."),style=MaterialTheme.typography.bodySmall)
        }
        item { Text(label(language, "Her atölye farklı bir çözüm yolu çalıştırır. Görevler başlangıçtan meydan okumaya ilerler.", "Each workshop practises a different solution process. Missions progress from a starter to a challenge.")) }
        if (Curriculum.hasFoundation(category)) item {
            val title = when (category) {
                GameCategory.ALGORITHM -> label(language, "Rota atölyesi", "Route workshop")
                GameCategory.ALPHABET, GameCategory.NUMBERS -> label(language, "Keşfet ve dinle", "Explore and listen")
                GameCategory.VISUAL -> label(language, "Gölgeden nesneye", "From silhouette to object")
                GameCategory.NUMERICAL -> label(language, "Bire bir sayma", "One-to-one counting")
                GameCategory.LOGIC -> label(language, "Tekrar eden gruplar", "Repeating groups")
                else -> label(language, "Şekillerin özellikleri", "Shape properties")
            }
            val foundationDone=com.algokids.data.ProgressStore(LocalContext.current).foundationCompleted(category,language.name)
            WorkshopCard(title, label(language, "Temel alıştırma · Set geçişi için aşağıdaki puanlı atölyeleri tamamla", "Foundation practice · Complete the scored workshops below to unlock the next set"),
                if(foundationDone) label(language,"✓ Temel alıştırma tamamlandı","✓ Foundation practice completed") else label(language, "Başla / devam et", "Start / continue"), onClassic)
        }
        items(ChallengeCatalog.forCategory(category).map { ChallengeLocalization.localize(it,language==AppLanguage.EN) }, key = { it.id }) { challenge ->
            val key = Curriculum.progressKey(challenge.id, language.name)
            val completed = LearningPath.workshopCompleted(prefs,challenge,language.name)
            val round = prefs.getInt("${key}_round", 0)
            WorkshopCard(
                label(language, challenge.titleTr, challenge.titleEn),
                label(language, challenge.skillTr, challenge.skillEn) + "\n" + label(language, "${challenge.rounds.size} görev · Farklı çözüm mekanikleri", "${challenge.rounds.size} missions · Distinct solution mechanics"),
                if (completed) label(language, "✓ Tamamlandı · Tekrar oyna", "✓ Completed · Play again")
                else if (round > 0) label(language, "Devam et · ${round + 1}. görev", "Continue · Mission ${round + 1}")
                else label(language, "Atölyeye başla", "Start workshop"),
                { onChallenge(challenge) }
            )
        }
        item { Text(label(language, "Birlikte oynarken: “Bu adımı neden seçtin?” ve “Başka bir geçerli çözüm var mı?” diye sorun.", "When playing together ask: ‘Why did you choose this step?’ and ‘Is there another valid solution?’"), style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun WorkshopCard(title: String, detail: String, action: String, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF516078))
            Text(action, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun ChallengeScreen(challenge: Challenge, language: AppLanguage, tts: TextToSpeech, isTtsReady: Boolean, isSoundEnabled: Boolean, onToggleSound: () -> Unit, onBack: () -> Unit,onComplete:()->Unit=onBack, ads:com.algokids.ads.WorkshopAds?=null) {
    val prefs = LocalContext.current.getSharedPreferences("algokids_progress", Context.MODE_PRIVATE)
    val progressKey = Curriculum.progressKey(challenge.id, language.name)
    var index by rememberSaveable(challenge.id,language) {
        mutableIntStateOf(if (prefs.getBoolean("${progressKey}_done", false)) 0 else prefs.getInt("${progressKey}_round", 0).coerceIn(0, challenge.rounds.lastIndex))
    }
    var finished by rememberSaveable(challenge.id,language) { mutableStateOf(false) }
    var correct by rememberSaveable(challenge.id, language, index) { mutableStateOf(false) }
    var feedback by rememberSaveable(challenge.id, language, index) { mutableStateOf("") }
    var mistakes by rememberSaveable(challenge.id,language) { mutableIntStateOf(challenge.rounds.indices.sumOf { prefs.getInt("${progressKey}_attempts_$it",0) }) }
    val memory = challenge.mode in setOf(ChallengeMode.RECALL, ChallengeMode.GRID_RECALL)
    val listening = challenge.mode in setOf(ChallengeMode.LISTEN, ChallengeMode.LISTEN_CHOICE)
    var preview by rememberSaveable(challenge.id, language, index) { mutableStateOf(memory) }
    val allowance=remember(prefs) {com.algokids.data.SolutionAllowance(prefs)}
    var remaining by remember {mutableIntStateOf(allowance.remaining())}
    var autoplay by rememberSaveable(challenge.id,language,index) {mutableStateOf(false)}
    var rewardConfirm by remember {mutableStateOf(false)}
    var rewardTick by remember {mutableIntStateOf(0)}
    LaunchedEffect(ads) {while(true) {kotlinx.coroutines.delay(1000);rewardTick++;remaining=allowance.remaining()}}
    var example by rememberSaveable(challenge.id, language, index) { mutableStateOf(false) }
    var solutionStep by rememberSaveable(challenge.id, language, index) { mutableIntStateOf(0) }
    var transcript by rememberSaveable(challenge.id, language, index) { mutableStateOf(false) }
    var response by rememberSaveable(challenge.id, language, index) { mutableStateOf(arrayListOf<String>()) }
    val round = challenge.rounds[index]
    val options = remember(challenge.id, index) { round.options.shuffled() }
    val ordered = challenge.mode in setOf(ChallengeMode.ORDER, ChallengeMode.RECALL, ChallengeMode.LISTEN)
    val gridMode = challenge.mode in setOf(ChallengeMode.GRID, ChallengeMode.GRID_RECALL)
    val scan = challenge.mode == ChallengeMode.SCAN
    val reusable = challenge.mechanic in setOf(LearningMechanic.BRANCH_EXECUTION, LearningMechanic.REVERSE_WORKING_MEMORY, LearningMechanic.SPOKEN_INSTRUCTIONS)
    val fullResponse = if (scan) response.size == round.stimulus.size else if (ordered) response.size == round.answer.size else response.isNotEmpty()
    fun spokenText() = if (listening) label(language, round.spokenTr, round.spokenEn) else label(language, round.promptTr, round.promptEn)
    fun narrate() { if (isTtsReady) speak(tts, spokenText(), language, isSoundEnabled) }
    fun select(token: String) {
        feedback = ""
        response = ArrayList(when {
            scan -> response + token
            ordered -> if (response.size < round.answer.size && (reusable || token !in response)) response + token else response
            gridMode || challenge.mode == ChallengeMode.MULTI -> if (token in response) response - token else response + token
            else -> listOf(token)
        })
    }
    LaunchedEffect(autoplay) {
        if(autoplay) {
            example=true;solutionStep=0
            for(step in 1..round.answer.size) {kotlinx.coroutines.delay(900);solutionStep=step}
            autoplay=false
        }
    }
    BackHandler(onBack = onBack)
    DisposableEffect(Unit) { onDispose { tts.stop() } }
    LaunchedEffect(challenge.id, index, isTtsReady, isSoundEnabled, language) { narrate() }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text(label(language, "‹ Atölyeler", "‹ Workshops")) }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onToggleSound) { Text(if (isSoundEnabled) label(language, "Sesi kapat", "Mute") else label(language, "Sesi aç", "Sound on")) }
        }
        Text(label(language, challenge.titleTr, challenge.titleEn), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        val stage = when (index) { 0 -> label(language, "Başlangıç", "Starter"); 1 -> label(language, "Gelişen", "Building"); else -> label(language, "Meydan okuma", "Challenge") }
        Text(label(language, "Görev ${index + 1}/${challenge.rounds.size} · $stage", "Mission ${index + 1}/${challenge.rounds.size} · $stage"), style = MaterialTheme.typography.labelLarge)
        LinearProgressIndicator(progress = { (index + if (correct) 1f else 0f) / challenge.rounds.size }, modifier = Modifier.fillMaxWidth())
        if (finished) {
            Text(label(language, "Atölye tamamlandı!", "Workshop complete!"), style = MaterialTheme.typography.headlineSmall)
            val examScore=challenge.rounds.indices.sumOf { prefs.getInt(LearningPath.key(challenge.id,language.name,it),0) } / challenge.rounds.size
            Text(label(language,"İlk değerlendirme başarısı: $examScore/100. Tekrarlar puanını silmez; becerini pekiştirir.","First assessment score: $examScore/100. Practice keeps this score and strengthens your skills."))
            Text(label(language, "${challenge.rounds.size} görevi çözdün; $mistakes kez çözümünü gözden geçirdin. Şimdi bir yetişkine kullandığın yöntemi anlat.", "You solved ${challenge.rounds.size} missions and revised your solution $mistakes times. Explain your method to an adult."))
            Text(label(language,"Atölyelere dönerken kısa bir reklam gösterilebilir.","A short advertisement may appear when returning to workshops."),style=MaterialTheme.typography.bodySmall)
            Button(onClick = onComplete, modifier = Modifier.fillMaxWidth().testTag("workshop_finished")) { Text(label(language, "Diğer atölyeleri keşfet", "Explore other workshops")) }
            return@Column
        }
        Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(signalWords(label(language, round.promptTr, round.promptEn), language), style = MaterialTheme.typography.titleMedium)
                val guidance = when(challenge.mechanic) {
                    LearningMechanic.LAYER_INTEGRATION -> label(language,"İki katmanı üst üste koyduğunu düşün. Herhangi bir katmanda mavi olan kareyi alttaki boş panoda seç. Aynı yerdeki iki mavi kareyi bir kez seç.","Imagine placing the two layers on top of each other. On the blank board, select every square that is blue in either layer. Select overlapping blue squares only once.")
                    LearningMechanic.MISSING_MOSAIC -> label(language,"Soldaki tam modeli sağdaki eksik modelle karşılaştır. Yalnızca tam modelde olup eksik modelde olmayan kareleri alttaki boş panoda seç.","Compare the full model on the left with the incomplete model on the right. On the blank board below, select only the squares missing from the incomplete model.")
                    LearningMechanic.RESPONSE_INHIBITION, LearningMechanic.RULE_SWITCHING -> label(language,"Her karttan önce üstteki kuralı oku. Kart kurala uyuyorsa Seç, uymuyorsa Geç düğmesine dokun. Kural değişebilir; her kartta yeniden bak.","Read the rule above each card. Tap Select if the card matches, or Skip if it does not. The rule may change; check it for every card.")
                    else -> ""
                }
                if(guidance.isNotEmpty()) Text(guidance,style=MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { narrate() }) { Text(if (listening) label(language, "Yönergeyi tekrar dinle", "Replay instruction") else label(language, "Yönergeyi dinle", "Read instruction")) }
                if (listening) {
                    TextButton(onClick = { transcript = !transcript }, modifier = Modifier.testTag("transcript")) { Text(label(language, "Yazılı yönergeyi göster / gizle", "Show / hide written instruction")) }
                    if (transcript) Text(label(language, round.spokenTr, round.spokenEn))
                    if (!isTtsReady || !isSoundEnabled || !configureVoice(tts,language)) {
                        Text(label(language, "Ses kullanılamıyorsa yazılı yönergeyle çalışabilirsin.", "If sound is unavailable, use the written instruction."), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (!gridMode && !scan && round.stimulus.isNotEmpty()) {
                    if (!memory || preview || correct) {
                        Text(round.stimulus.mapIndexed { i, s -> if (challenge.mechanic == LearningMechanic.FIRST_BUG) "${i + 1}: $s" else s }.joinToString("   "), fontSize = 26.sp)
                    } else Text(label(language, "Sıra gizlendi. Şimdi hatırla.", "Sequence hidden. Now recall it."))
                }
                if (memory) {
                    TextButton(onClick = { preview = !preview; response = arrayListOf(); feedback = "" }, enabled = !correct, modifier = Modifier.testTag("memory_toggle")) {
                        Text(if (preview) label(language, "Hazırım, gizle", "Ready, hide it") else label(language, "Yeniden incele", "Study again"))
                    }
                }
            }
        }
        if (gridMode) {
            val task = requireNotNull(round.grid)
            if (challenge.mode != ChallengeMode.GRID_RECALL || preview || correct) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                task.panels.forEachIndexed { panelIndex, cells ->
                    val panelTitle = when (challenge.mechanic) {
                        LearningMechanic.MISSING_MOSAIC -> if (panelIndex == 0) label(language, "Tam model", "Full model") else label(language, "Eksik model", "Incomplete model")
                        LearningMechanic.LAYER_INTEGRATION -> label(language, "Katman ${panelIndex + 1}", "Layer ${panelIndex + 1}")
                        LearningMechanic.LOCATION_RECALL -> label(language, "Hatırlanacak yerler", "Locations to remember")
                        else -> label(language, "Başlangıç şekli", "Starting shape")
                    }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(panelTitle, fontWeight = FontWeight.Bold)
                        PuzzleGrid(task.size, cells, false, language, "reference_$panelIndex") {}
                    }
                }
                }
            } else Text(label(language, "Yerler gizlendi. Boş panoda hatırladığın kareleri seç.", "Locations hidden. Choose the remembered squares on the blank board."))
            if (!preview) {
                Text(label(language, "Çözüm panon", "Your solution board"), fontWeight = FontWeight.Bold)
                PuzzleGrid(task.size, response.mapNotNull(String::toIntOrNull).toSet(), !correct, language, "answer") { select(it.toString()) }
            }
        } else if (scan) {
            val step = response.size
            if (step < round.stimulus.size) {
                Text(label(language, round.stageRulesTr[step], round.stageRulesEn[step]).let { signalWords(it, language) }, fontWeight = FontWeight.Bold, modifier = Modifier.testTag("scan_rule"))
                Text(label(language, "Kart ${step + 1}/${round.stimulus.size}", "Card ${step + 1}/${round.stimulus.size}"))
                SignalCard(round.stimulus[step], language)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { select("✓") }, enabled = !correct, modifier = Modifier.weight(1f).testTag("scan_select")) { Text(label(language, "Seç", "Select")) }
                    OutlinedButton(onClick = { select("–") }, enabled = !correct, modifier = Modifier.weight(1f).testTag("scan_skip")) { Text(label(language, "Geç", "Skip")) }
                }
            } else Text(label(language, "Akış bitti. Kararlarını kontrol et.", "Stream finished. Check your decisions."))
            Text(label(language, "Kararların: ", "Your decisions: ") + response.joinToString(" "))
        } else {
            Text(
                if (ordered) label(language, if (reusable) "Kartlara sırayla dokun. Aynı kart birden fazla kullanılabilir." else "Kartlara sırayla dokun. Her kartı bir kez kullan.", if (reusable) "Tap cards in order. A card can be reused." else "Tap cards in order. Use each card once.")
                else label(language, "Bir yanıt seç, ardından kontrol et.", "Choose an answer, then check it.")
            )
            options.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { token ->
                        val selected = token in response
                        val labels = if (language == AppLanguage.TR) round.labelsTr else round.labelsEn
                        FilledTonalButton(
                            onClick = { select(token) }, enabled = !correct && !preview && (reusable || !ordered || !selected),
                            modifier = Modifier.weight(1f).heightIn(min = 60.dp).testTag("choice_$token"), shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = if (selected) Color(0xFFCCEADF) else Color(0xFFE5EAF5))
                        ) { Text(if (labels[token] == null) token else "$token ${labels[token]}", fontSize = if (labels.isEmpty()) 22.sp else 16.sp) }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            if (response.isNotEmpty()) Text(label(language, "Çözümün: ", "Your solution: ") + response.joinToString(if (ordered) " → " else ", "), fontWeight = FontWeight.Bold)
        }
        if (feedback.isNotEmpty()) Surface(color = if (correct) Color(0xFFD7F4EA) else Color(0xFFFFEFD8), shape = RoundedCornerShape(16.dp)) {
            Text(feedback, Modifier.fillMaxWidth().padding(16.dp).testTag("feedback"))
        }
        Text(label(language,"Bugün $remaining otomatik gösterim hakkın var. Adımları kendin incelemek her zaman ücretsiz.","Automatic demonstrations left today: $remaining. Explore the steps for free."),style=MaterialTheme.typography.bodySmall)
        Button(onClick={if(allowance.use()) {remaining=allowance.remaining();tts.stop();autoplay=true}},enabled=remaining>0 && !autoplay,modifier=Modifier.fillMaxWidth().testTag("automatic_solution")) {
            Text(label(language,"Çözümü adım adım oynat","Play the solution step by step"))
        }
        if(remaining==0 && ads!=null) {
            OutlinedButton(onClick={rewardConfirm=true},enabled=rewardTick.let {ads.rewardReady},modifier=Modifier.fillMaxWidth().testTag("reward_solution")) {
                Text(label(language,"İsteğe bağlı reklam · 1 ek gösterim","Optional ad · 1 extra demonstration"))
            }
            if(!ads.rewardReady)Text(label(language,"Şu an reklam hazır değil. Ücretsiz adımları inceleyebilirsin.","No ad is ready right now. You can explore the free steps."))
        }
        OutlinedButton(onClick={autoplay=false;example=!example;solutionStep=0;tts.stop()},modifier=Modifier.fillMaxWidth().testTag("example_solution")) { Text(label(language,"Nasıl çözülür? · Adım adım göster","How to solve · Show steps")) }
        if(example) Surface(shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.surfaceVariant) {
            Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text(label(language,"Birlikte inceleyelim","Let’s explore together"),fontWeight=FontWeight.Bold)
                Text(label(language,round.explanationTr,round.explanationEn))
                if(gridMode) {
                    Text(label(language,"Mavi kareleri çözüm panosunda seç. Boş karelere dokunma.","Select the blue squares on your solution board. Leave empty squares alone."))
                    PuzzleGrid(requireNotNull(round.grid).size,round.answer.take(solutionStep).mapNotNull(String::toIntOrNull).toSet(),false,language,"example") {}
                } else {
                    val labels=if(language==AppLanguage.TR)round.labelsTr else round.labelsEn
                    round.answer.take(solutionStep).forEachIndexed { step,token -> Text(if(scan) label(language,"Kart ${step+1}: ${signalWords(round.stimulus[step],language)} · ${if(token=="✓") "Seç" else "Geç"}","Card ${step+1}: ${signalWords(round.stimulus[step],language)} · ${if(token=="✓") "Select" else "Skip"}") else "${step+1}. ${labels[token] ?: token}") }
                }
                if(solutionStep<round.answer.size) Button(onClick={solutionStep++},enabled=!autoplay) {Text(label(language,"Sonraki çözüm adımı","Next solution step"))}
                else Text(label(language,"Şimdi kendi panonda dene. Örneği incelemek puan veya tamamlanma kazandırmaz.","Now try on your own board. Viewing the example does not award points or completion."))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { response = ArrayList(response.dropLast(1)); feedback = "" }, enabled = !correct && response.isNotEmpty(), modifier = Modifier.weight(1f).testTag("undo")) { Text(label(language, "Geri al", "Undo")) }
            Button(
                onClick = {
                    if (correct) {
                        if (index == challenge.rounds.lastIndex) {
                            finished = true
                            prefs.edit().putBoolean("${progressKey}_done", true).putInt("${progressKey}_round", 0).apply()
                        } else { index++; prefs.edit().putInt("${progressKey}_round", index).apply() }
                    } else if (round.accepts(response.toList(), challenge.mode)) {
                        correct = true
                        val examKey=LearningPath.key(challenge.id,language.name,index)
                        if(!prefs.contains(examKey)) prefs.edit().putInt(examKey,LearningPath.earned(prefs.getInt("${progressKey}_attempts_$index",0))).apply()
                        if(index==challenge.rounds.lastIndex) prefs.edit().putBoolean("${progressKey}_done",true).putInt("${progressKey}_round",0).apply()
                        feedback = label(language, "Çözdün! ${round.explanationTr}", "Solved! ${round.explanationEn}")
                        if (isTtsReady) speak(tts, label(language,"Çözdün!","Solved!"), language, isSoundEnabled)
                    } else {
                        mistakes++
                        val attemptsKey="${progressKey}_attempts_$index"
                        prefs.edit().putInt(attemptsKey,prefs.getInt(attemptsKey,0)+1).apply()
                        feedback = label(language, "Henüz olmadı. " + when {
                            gridMode -> "Satır ve sütunları karşılaştır; fazla veya eksik bir kare var mı?"
                            scan -> "Her kararı o adımda görünen hedefe göre yeniden incele."
                            ordered -> "Adımların sırasını, tekrar sayısını ve bütün ipuçlarını kontrol et."
                            else -> "Bütün ipuçlarını birlikte kontrol et."
                        }, "Not yet. " + when {
                            gridMode -> "Compare rows and columns. Are any squares missing or extra?"
                            scan -> "Check each decision against the target shown at that step."
                            ordered -> "Check the order, repetition counts, and all the clues."
                            else -> "Check all the clues together."
                        })
                    }
                }, enabled = correct || (!preview && fullResponse), modifier = Modifier.weight(1f).testTag("check")
            ) { Text(if (correct) label(language, "Devam et", "Continue") else label(language, "Kontrol et", "Check")) }
        }
    }
    if(rewardConfirm && ads!=null) AlertDialog(onDismissRequest={rewardConfirm=false},
        title={Text(label(language,"Ödüllü reklam","Rewarded advertisement"))},
        text={Text(label(language,"Reklam ödülü kazanıldığında 1 ek otomatik gösterim hakkı gelir. Erken kapatınca hak gelmeyebilir. Ücretsiz örnek adımları her zaman inceleyebilirsin.","Earning the ad reward adds one automatic demonstration. Closing early may give no reward. The free example steps are always available."))},
        confirmButton={TextButton(onClick={rewardConfirm=false;ads.rewardSolution({tts.stop()}) {remaining=allowance.remaining()}},modifier=Modifier.testTag("confirm_solution_reward")) {Text(label(language,"Reklamı başlat","Start advertisement"))}},
        dismissButton={TextButton(onClick={rewardConfirm=false}) {Text(label(language,"Vazgeç","Cancel"))}})

}

@Composable
private fun PuzzleGrid(size: Int, cells: Set<Int>, enabled: Boolean, language: AppLanguage, prefix: String, onCell: (Int) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(18.dp), modifier = Modifier.widthIn(max = if (prefix == "answer") 260.dp else 180.dp).fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(size) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(size) { col ->
                        val cell = row * size + col
                        val description = label(language, "Satır ${row + 1}, sütun ${col + 1}", "Row ${row + 1}, column ${col + 1}")
                        if (prefix == "answer") {
                            FilledTonalButton(onClick = { onCell(cell) }, enabled = enabled, contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.weight(1f).aspectRatio(1f).testTag("cell_$cell").semantics { contentDescription = description },
                                shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (cell in cells) Color(0xFF315DB0) else Color(0xFFEDF1FA),
                                    disabledContainerColor = if (cell in cells) Color(0xFF315DB0) else Color(0xFFEDF1FA))) {
                                Text(if (cell in cells) "●" else "", color = Color.White)
                            }
                        } else Box(Modifier.weight(1f).aspectRatio(1f).background(if (cell in cells) Color(0xFF315DB0) else Color(0xFFEDF1FA), RoundedCornerShape(10.dp)).semantics { contentDescription = "$description: ${if (cell in cells) "●" else "○"}" })
                    }
                }
            }
        }
    }
}

private fun signalWords(text: String, language: AppLanguage) = text
    .replace("🔵▲", label(language, "mavi üçgen", "blue triangle"))
    .replace("🔴▲", label(language, "kırmızı üçgen", "red triangle"))
    .replace("🔵●", label(language, "mavi daire", "blue circle"))
    .replace("🔴●", label(language, "kırmızı daire", "red circle"))

@Composable
private fun SignalCard(token: String, language: AppLanguage) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(100.dp).semantics { contentDescription = signalWords(token, language) }) {
                val color = if (token.startsWith("🔵")) Color(0xFF315DB0) else Color(0xFFD14949)
                if (token.endsWith("▲")) drawPath(Path().apply { moveTo(size.width / 2, 0f); lineTo(size.width, size.height); lineTo(0f, size.height); close() }, color)
                else drawCircle(color)
            }
        }
    }
}
