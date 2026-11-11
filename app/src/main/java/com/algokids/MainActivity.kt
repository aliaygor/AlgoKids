package com.algokids

import android.os.Bundle
import android.content.Context
import android.speech.tts.TextToSpeech
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.algokids.data.ContentRepository
import com.algokids.data.ProgressStore
import com.algokids.game.model.*
import com.algokids.ui.screens.*
import com.algokids.ui.theme.AlgoKidsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var workshopAds:com.algokids.ads.WorkshopAds
    private val notificationPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()) { com.algokids.notifications.ReminderScheduler.schedule(this) }
    private var reminderCategory by mutableStateOf<GameCategory?>(null)
    private fun consumeReminder(intent:android.content.Intent):GameCategory? {
        val category=runCatching { GameCategory.valueOf(intent.getStringExtra("resume_category") ?: "") }.getOrNull()
        intent.removeExtra("resume_category")
        return category
    }
    override fun onStart() { super.onStart();if(::workshopAds.isInitialized)workshopAds.resume(); com.algokids.notifications.ReminderScheduler.visit(this) }
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if(BuildConfig.DEBUG && intent.action==android.content.Intent.ACTION_MAIN && intent.hasCategory(android.content.Intent.CATEGORY_LAUNCHER)) {
            getSharedPreferences("algokids_progress",Context.MODE_PRIVATE).edit().remove("ads_disabled_for_tests").apply()
            if(::workshopAds.isInitialized) workshopAds.resume()
        }
        com.algokids.notifications.ReminderScheduler.visit(this)
        reminderCategory=consumeReminder(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(BuildConfig.DEBUG && savedInstanceState==null) {
            // Instrumentation tests use this temporary switch. Never let it persistently suppress debug test ads on a fresh launch.
            getSharedPreferences("algokids_progress",Context.MODE_PRIVATE).edit().remove("ads_disabled_for_tests").apply()
        }
        workshopAds=com.algokids.ads.WorkshopAds(this,savedInstanceState==null)
        reminderCategory=consumeReminder(intent)
        val reminderPrefs=com.algokids.notifications.ReminderScheduler.prefs(this)
        com.algokids.speech.ChildNarration.loadPreferences(reminderPrefs)
        if(Build.VERSION.SDK_INT>=33 && !reminderPrefs.getBoolean("notification_permission_asked_v1",false)) {
            reminderPrefs.edit().putBoolean("notification_permission_asked_v1",true).apply()
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        enableEdgeToEdge()
        setContent {
            AlgoKidsTheme {
                val preferences = remember { getSharedPreferences("algokids_progress",Context.MODE_PRIVATE) }
                val repo = remember { ContentRepository(this) }
                val store = remember { ProgressStore(this) }
                var selectedCategory by rememberSaveable { mutableStateOf<GameCategory?>(null) }
                var parentSettings by rememberSaveable { mutableStateOf(false) }
                var appearanceVisible by rememberSaveable { mutableStateOf(false) }
                var appearanceReturnToParent by rememberSaveable { mutableStateOf(false) }
                var dailyVisible by rememberSaveable { mutableStateOf(false) }
                var classic by rememberSaveable { mutableStateOf(false) }
                var challengeId by rememberSaveable { mutableStateOf<String?>(null) }
                var storiesVisible by rememberSaveable { mutableStateOf(false) }
                var progressRevision by remember { mutableIntStateOf(0) }
                var selectedStoryId by rememberSaveable { mutableStateOf<String?>(null) }
                val selectedStory = com.algokids.data.StoryCatalog.all.find { it.id == selectedStoryId }
                var language by rememberSaveable { mutableStateOf(runCatching { AppLanguage.valueOf(preferences.getString("language","TR") ?: "TR") }.getOrDefault(AppLanguage.TR)) }
                var isSoundEnabled by rememberSaveable { mutableStateOf(preferences.getBoolean("sound",true)) }
                var isTtsReady by remember { mutableStateOf(false) }
                val sharedTts = remember { TextToSpeech(this) { status -> isTtsReady = status == TextToSpeech.SUCCESS } }
                DisposableEffect(Unit) { onDispose { sharedTts.stop(); sharedTts.shutdown() } }
                DisposableEffect(selectedCategory, classic, challengeId, selectedStory) { onDispose { sharedTts.stop() } }
                LaunchedEffect(selectedCategory) { selectedCategory?.let { preferences.edit().putString("recent_category",it.name).apply() } }
                LaunchedEffect(language,isSoundEnabled) { if(!isSoundEnabled) sharedTts.stop(); preferences.edit().putString("language",language.name).putBoolean("sound",isSoundEnabled).apply() }
                LaunchedEffect(isTtsReady, language) { if(isTtsReady) configureVoice(sharedTts,language) }
                val sessions = remember { GameCategory.entries.associateWith { store.load(it) } }
                sessions.forEach { (category,session) ->
                    LaunchedEffect(session) {
                        snapshotFlow { store.snapshot(session) }.collect { snapshot ->
                            withContext(Dispatchers.IO) { store.save(category, snapshot) }
                        }
                    }
                }
                LaunchedEffect(reminderCategory) { reminderCategory?.let { if(com.algokids.data.LearningPath.unlocked(preferences,it,language.name)) selectedCategory=it;classic=false;challengeId=null;parentSettings=false;appearanceVisible=false;dailyVisible=false;selectedStoryId=null;storiesVisible=false;reminderCategory=null } }
                val challenge=ChallengeCatalog.all.find { it.id==challengeId }?.let { ChallengeLocalization.localize(it,language==AppLanguage.EN) }
                when {
                    appearanceVisible -> AppearanceScreen(language,workshopAds,{sharedTts.stop()},{
                        appearanceVisible=false
                        if(appearanceReturnToParent) parentSettings=true
                        appearanceReturnToParent=false
                    })
                    parentSettings -> ParentSettingsScreen(language,sharedTts,isTtsReady,{parentSettings=false},workshopAds.privacyRequired(),{workshopAds.privacyOptions()}, {com.algokids.ads.ParentBanner(workshopAds,language)}, {
                        appearanceReturnToParent=true
                        parentSettings=false
                        appearanceVisible=true
                    })
                    dailyVisible -> DailyMissionScreen(language,{dailyVisible=false})
                    selectedStory != null -> StoryScreen(selectedStory!!,language,isSoundEnabled,sharedTts,isTtsReady,{isSoundEnabled=!isSoundEnabled},{selectedStoryId=null})
                    challenge != null -> key(challenge.id) { ChallengeScreen(challenge,language,sharedTts,isTtsReady,isSoundEnabled,{isSoundEnabled=!isSoundEnabled},{challengeId=null},
                        {workshopAds.afterWorkshop(challenge.id,{sharedTts.stop()},{challengeId=null})},workshopAds) }
                    selectedCategory != null && !classic -> CategoryScreen(selectedCategory!!,language,{classic=true},{challengeId=it.id},{selectedCategory=null})
                    selectedCategory != null -> {
                        val category=selectedCategory!!
                        when(category) {
                            GameCategory.ALGORITHM -> AlgorithmGameScreen(language,isSoundEnabled,sharedTts,isTtsReady,{isSoundEnabled=!isSoundEnabled},{classic=false})
                            GameCategory.ALPHABET,GameCategory.NUMBERS -> LearningScreen(category,language,isSoundEnabled,sharedTts,isTtsReady,{classic=false})
                            else -> {
                                val list by produceState<List<GameContent>?>(null, category, repo) {
                                    value = withContext(Dispatchers.IO) { repo.getContentByCategory(category) }
                                }
                                if (list == null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                } else {
                                    GameScreen(list.orEmpty(),sessions.getValue(category),language,isSoundEnabled,sharedTts,isTtsReady,{isSoundEnabled=!isSoundEnabled},{classic=false})
                                }
                            }
                        }
                    }
                    else -> HomeScreen(
                        bannerContent={com.algokids.ads.ParentBanner(workshopAds,language,"home_banner")},
                        onAppearance={appearanceReturnToParent=false;appearanceVisible=true},
                        progressRevision=progressRevision,onResetProgress={
                            sharedTts.stop(); sessions.values.forEach { it.restart() };store.resetLearning();progressRevision++
                        },
                        onDailyMission={dailyVisible=true},onParentSettings={parentSettings=true},storiesVisible=storiesVisible,onStoriesVisibleChange={storiesVisible=it},
                        language=language,onLanguageChange={language=it},onCategorySelect={if(com.algokids.data.LearningPath.unlocked(preferences,it,language.name)) {selectedCategory=it;classic=false}},onStorySelect={selectedStoryId=it.id},
                        onChallengeSelect={if(com.algokids.data.LearningPath.unlocked(preferences,it.category,language.name)) {selectedCategory=it.category; challengeId=it.id}},
                        performanceSummary=buildList {
                            sessions.filterValues { it.correctCount>0 || it.mistakeCount>0 }.forEach { (category,session) ->
                                add("${categoryTitle(category,language)}: ${label(language,"Tamamlanan","Solved")} ${session.correctCount}, ${label(language,"Yeniden deneme","Revisions")} ${session.mistakeCount}")
                            }
                            val routeCount=com.algokids.game.engine.RouteLevels.all.indices.count { preferences.getBoolean("route_done_$it",false) }
                            if(routeCount>0) add(label(language,"Rota atölyesi: $routeCount bölüm tamamlandı","Route workshop: $routeCount levels completed"))
                            com.algokids.data.LearningPath.order.filter { com.algokids.data.LearningPath.solved(preferences,it,language.name)>0 }.forEach { category ->
                                add("${categoryTitle(category,language)}: ${com.algokids.data.LearningPath.score(preferences,category,language.name)}/100")
                            }
                            ChallengeCatalog.all.filter { preferences.getBoolean("${com.algokids.data.Curriculum.progressKey(it.id, language.name)}_done",false) }.map { ChallengeLocalization.localize(it,language==AppLanguage.EN) }.forEach { add("✓ ${label(language,it.titleTr,it.titleEn)}") }
                        }
                    )
                }
            }
        }
        workshopAds.start()
    }
    override fun onDestroy() { if(::workshopAds.isInitialized)workshopAds.destroy();super.onDestroy() }
}
