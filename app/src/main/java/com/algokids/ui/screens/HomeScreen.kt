package com.algokids.ui.screens

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.model.*

@Composable
fun HomeScreen(
    language: AppLanguage, onLanguageChange: (AppLanguage) -> Unit,
    onCategorySelect: (GameCategory) -> Unit, onStorySelect: (Story) -> Unit,
    performanceSummary: List<String> = emptyList(), onChallengeSelect: (Challenge) -> Unit = {},
    onParentSettings: () -> Unit = {}, onDailyMission: () -> Unit = {}, storiesVisible: Boolean = false, onStoriesVisibleChange: (Boolean) -> Unit = {},
    progressRevision:Int=0,onResetProgress:()->Unit={},onAppearance:()->Unit={},
    bannerContent:@Composable ()->Unit={}
) {
    var showPrivacy by remember { mutableStateOf(false) }
    var showPerformance by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val prefs = context.getSharedPreferences("algokids_progress",Context.MODE_PRIVATE)
    val progressStore = remember(context) { com.algokids.data.ProgressStore(context) }
    val progressSnapshot = remember(prefs, progressStore, language, progressRevision) {
        val categories = com.algokids.data.LearningPath.order
        val skillProgress = categories.map { category ->
            com.algokids.data.LearningPath.progress(prefs, category, language.name, progressStore.foundationCompleted(category, language.name))
        }
        val allMissions = categories.sumOf { category ->
            com.algokids.data.LearningPath.total(category) + if (com.algokids.data.Curriculum.hasFoundation(category)) 3 else 0
        }
        val completedMissions = categories.sumOf { category ->
            com.algokids.data.LearningPath.solved(prefs, category, language.name) +
                if (progressStore.foundationCompleted(category, language.name) && com.algokids.data.Curriculum.hasFoundation(category)) 3 else 0
        }
        HomeProgressSnapshot(
            skillProgress = skillProgress,
            allMissions = allMissions,
            completedMissions = completedMissions,
            overallProgress = if (allMissions == 0) 0 else (completedMissions * 100 / allMissions).coerceIn(0, 100),
            completedWorkshops = ChallengeCatalog.all.count { com.algokids.data.LearningPath.workshopCompleted(prefs, it, language.name) },
            playerLevel = com.algokids.data.LearningPath.level(prefs, language.name)
        )
    }
    val skillProgress = progressSnapshot.skillProgress
    val allMissions = progressSnapshot.allMissions
    val completedMissions = progressSnapshot.completedMissions
    val overallProgress = progressSnapshot.overallProgress
    val completedWorkshops = progressSnapshot.completedWorkshops
    val playerLevel = progressSnapshot.playerLevel
    val recent = remember(progressRevision) { runCatching { GameCategory.valueOf(prefs.getString("recent_category", "") ?: "") }.getOrNull() }
    val resume = com.algokids.data.LearningPath.nextCategory(prefs,language.name,recent)
    BackHandler { if(storiesVisible) onStoriesVisibleChange(false) else showExit=true }
    val categories = listOf(
        CategoryItem(categoryTitle(GameCategory.ALGORITHM,language),Icons.Default.AccountTree,Color(0xFF277D86),GameCategory.ALGORITHM),
        CategoryItem(categoryTitle(GameCategory.LOGIC,language),Icons.Default.Lightbulb,Color(0xFFB97919),GameCategory.LOGIC),
        CategoryItem(categoryTitle(GameCategory.NUMERICAL,language),Icons.Default.Calculate,Color(0xFF3D68B5),GameCategory.NUMERICAL),
        CategoryItem(categoryTitle(GameCategory.ATTENTION,language),Icons.Default.Psychology,Color(0xFF308263),GameCategory.ATTENTION),
        CategoryItem(categoryTitle(GameCategory.MEMORY,language),Icons.Default.Memory,Color(0xFF8256A3),GameCategory.MEMORY),
        CategoryItem(categoryTitle(GameCategory.VISUAL,language),Icons.Default.Visibility,Color(0xFFC76A48),GameCategory.VISUAL),
        CategoryItem(categoryTitle(GameCategory.AUDIOLOGY,language),Icons.Default.VolumeUp,Color(0xFF277D86),GameCategory.AUDIOLOGY),
        CategoryItem(categoryTitle(GameCategory.GEOMETRY,language),Icons.Default.Category,Color(0xFF5765A8),GameCategory.GEOMETRY),
        CategoryItem(categoryTitle(GameCategory.ALPHABET,language),Icons.Default.Abc,Color(0xFFB34D79),GameCategory.ALPHABET),
        CategoryItem(categoryTitle(GameCategory.NUMBERS,language),Icons.Default.Pin,Color(0xFF7657A3),GameCategory.NUMBERS)
    ).sortedBy { com.algokids.data.LearningPath.order.indexOf(it.type) }
    val sampleStories = com.algokids.data.StoryCatalog.ordered(language==AppLanguage.EN)

    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF8ED),Color(0xFFF1FAF6),Color(0xFFFFF8ED)))).safeDrawingPadding()) {
    LazyVerticalGrid(columns=GridCells.Adaptive(156.dp),modifier=Modifier.weight(1f).fillMaxWidth(),contentPadding=PaddingValues(20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item(span={GridItemSpan(maxLineSpan)}) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Surface(shape=RoundedCornerShape(18.dp),color=Color.White,shadowElevation=3.dp) {
                    androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(com.algokids.R.drawable.ic_app_logo),label(language,"AlgoKids logosu","AlgoKids logo"),Modifier.padding(5.dp).size(46.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("AlgoKids",fontWeight=FontWeight.ExtraBold,fontSize=28.sp,color=Color(0xFF174E52))
                    Text(label(language,"Merak et · Dene · Keşfet","Wonder · Try · Discover"),style=MaterialTheme.typography.labelMedium,color=Color(0xFF52706F))
                }
                IconButton(onClick={showPerformance=true},modifier=Modifier.background(Color(0xFFDDF3E9),CircleShape)) { Icon(Icons.Default.Assessment,label(language,"Gelişim özeti","Progress"),tint=Color(0xFF167C80)) }
                IconButton(onClick=onParentSettings,modifier=Modifier.background(Color(0xFFFFE8D7),CircleShape)) { Icon(Icons.Default.PrivacyTip,label(language,"Ebeveyn bilgisi","Parent information"),tint=Color(0xFFB95739)) }
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {
            val sunset=prefs.getString("cosmetic_theme","ocean")=="sunset"
            Card(shape=RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=Color.Transparent)) {
                Row(Modifier.fillMaxWidth().background(
                    Brush.linearGradient(if(sunset) listOf(Color(0xFF82494F),Color(0xFFB56D50)) else listOf(Color(0xFF167C80),Color(0xFF45A58E))),
                    RoundedCornerShape(30.dp)
                ).padding(20.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(9.dp)) {
                        Surface(color=Color.White.copy(alpha=.18f),shape=RoundedCornerShape(50)) {
                            Text(label(language,"BUGÜNÜN MACERASI","TODAY’S ADVENTURE"),color=Color.White,style=MaterialTheme.typography.labelMedium,fontWeight=FontWeight.Bold,modifier=Modifier.padding(horizontal=11.dp,vertical=6.dp))
                        }
                        Text(label(language,"Yaşına uygun günlük keşif","A daily discovery for your age"),fontWeight=FontWeight.ExtraBold,fontSize=24.sp,lineHeight=29.sp,color=Color.White)
                        Text(label(language,"Seviye ${com.algokids.data.LearningPath.level(prefs,language.name)}  ·  ${prefs.getInt("bonus_points",0)} keşif puanı","Level ${com.algokids.data.LearningPath.level(prefs,language.name)}  ·  ${prefs.getInt("bonus_points",0)} discovery points"),color=Color(0xFFE7FFF4),style=MaterialTheme.typography.bodyMedium)
                    }
                    Surface(shape=CircleShape,color=Color.White.copy(alpha=.16f),modifier=Modifier.size(68.dp)) {
                        Box(contentAlignment=Alignment.Center) { Icon(Icons.Default.AutoAwesome,null,tint=Color(0xFFFFE7A3),modifier=Modifier.size(38.dp)) }
                    }
                }
                Button(onClick=onDailyMission,colors=ButtonDefaults.buttonColors(containerColor=if(sunset) Color(0xFFFFD98F) else Color(0xFFFFD166),contentColor=Color(0xFF42351E)),shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp).heightIn(min=56.dp)) {
                    Icon(Icons.Default.Star,null,modifier=Modifier.size(19.dp)); Spacer(Modifier.width(8.dp)); Text(label(language,"Günlük meydan okumaya başla","Start today’s challenge"),fontWeight=FontWeight.Bold)
                }
            }
        }
        if(resume!=null) item(span={GridItemSpan(maxLineSpan)}) {
            Card(onClick={onCategorySelect(resume)},shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(defaultElevation=2.dp),modifier=Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal=17.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Surface(shape=CircleShape,color=Color(0xFFFFE7C2),modifier=Modifier.size(42.dp)) { Box(contentAlignment=Alignment.Center) { Icon(Icons.Default.PlayArrow,null,tint=Color(0xFFB95739)) } }
                    Column(Modifier.weight(1f)) { Text(label(language,"Kaldığın yerden","Pick up where you left off"),style=MaterialTheme.typography.labelMedium,color=Color(0xFF688078)); Text(categoryTitle(resume,language),fontWeight=FontWeight.Bold,color=Color(0xFF20334A)) }
                    Icon(Icons.Default.ChevronRight,null,tint=Color(0xFF167C80))
                }
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                FilterChip(selected=!storiesVisible,onClick={onStoriesVisibleChange(false)},label={Text(label(language,"Atölyeler","Workshops"))})
                FilterChip(selected=storiesVisible,onClick={onStoriesVisibleChange(true)},label={Text(label(language,"Hikâyeler","Stories"))})
                Spacer(Modifier.weight(1f))
                TextButton(onClick={onLanguageChange(if(language==AppLanguage.TR) AppLanguage.EN else AppLanguage.TR)}) { Text(if(language==AppLanguage.TR) "TR / EN" else "EN / TR") }
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {
            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                if(!storiesVisible) OutlinedButton(onClick={showReset=true},modifier=Modifier.testTag("reset_learning")) { Text(label(language,"Sıfırla","Reset progress")) }
                Text(if(storiesVisible) label(language,"Kısadan uzuna hikâyeler","Stories from short to long") else label(language,"Hangi becerini çalıştıralım?","What shall we practise?"),fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleLarge)
                Text(if(storiesVisible) label(language,"Her sayfada yeni bir adım. Sesli anlatımı tekrar dinleyebilirsin.","A new step on every page. Replay the narration whenever you like.") else label(language,"Kategorilerin içinde farklı çözüm yolları olan düşünme atölyeleri var.","Explore thinking workshops with different solution processes in each category."),color=Color(0xFF516078),style=MaterialTheme.typography.bodyMedium)
            }
        }
        if(storiesVisible) items(sampleStories,key={it.id},span={GridItemSpan(maxLineSpan)}) { story ->
            Card(onClick={onStorySelect(story)},shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                Row(Modifier.fillMaxWidth().padding(20.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.AutoStories,null,tint=Color(0xFF3D68B5))
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Text(label(language,story.title,story.titleEn),fontWeight=FontWeight.Bold)
                        val minutes=com.algokids.data.StoryCatalog.minutes(story,language==AppLanguage.EN)
                        Text(label(language,"Yaklaşık $minutes dk · ${story.pages.size} sayfa","About $minutes min · ${story.pagesEn.size} pages"),style=MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight,null)
                }
            }
        } else items(categories,key={it.type}) { category ->
            val unlocked=com.algokids.data.LearningPath.unlocked(prefs,category.type,language.name)
            val completed=com.algokids.data.LearningPath.completed(prefs,category.type,language.name)
            Column(verticalArrangement=Arrangement.spacedBy(7.dp)) {
                val number=com.algokids.data.LearningPath.order.indexOf(category.type)+1
                Surface(shape=RoundedCornerShape(50),color=if(!unlocked) Color(0xFFE8ECEB) else category.color.copy(alpha=.12f)) {
                    Row(Modifier.padding(horizontal=10.dp,vertical=5.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                        if(completed) Icon(Icons.Default.CheckCircle,null,tint=category.color,modifier=Modifier.size(14.dp))
                        Text(label(language,"Set $number · ${if(!unlocked) "Kilitli" else if(completed) "Tamamlandı" else "Açık"}","Set $number · ${if(!unlocked) "Locked" else if(completed) "Completed" else "Unlocked"}"),style=MaterialTheme.typography.labelMedium,color=if(unlocked) category.color else Color(0xFF78868A),fontWeight=FontWeight.SemiBold)
                    }
                }
                CategoryCard(category,language,unlocked) { onCategorySelect(category.type) }
                val progress=com.algokids.data.LearningPath.progress(prefs,category.type,language.name,com.algokids.data.ProgressStore(context).foundationCompleted(category.type,language.name))
                if(unlocked) Surface(shape=RoundedCornerShape(16.dp),color=Color.White.copy(alpha=.82f)) {
                    Column(Modifier.fillMaxWidth().padding(horizontal=13.dp,vertical=11.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text(label(language,"Tamamlanan: $progress/100","Completed: $progress/100"),fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
                        LinearProgressIndicator(progress={progress/100f},modifier=Modifier.fillMaxWidth().height(8.dp))
                    }
                } else Text(label(language,"Önce önceki eğitim setini tamamla.","Complete the preceding learning set first."),style=MaterialTheme.typography.bodySmall)
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {
            Card(
                onClick = onParentSettings,
                modifier = Modifier.fillMaxWidth().testTag("parent_settings_entry"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = .86f), modifier = Modifier.size(46.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("👨‍👩‍👧", fontSize = 23.sp) }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(label(language, "Ebeveyn köşesi", "Parent corner"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(label(language, "Hatırlatıcılar, gizlilik ve uygulama bilgileri", "Reminders, privacy and app information"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {
            Card(
                onClick = onAppearance,
                modifier = Modifier.fillMaxWidth().testTag("appearance_options"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = .85f), modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("🎨", fontSize = 23.sp) }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(label(language, "Renk dünyanı seç", "Choose your colour world"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text(label(language, "Okyanus ya da Gün batımı", "Ocean or Sunset"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) { Text(label(language,"Önce planla → Dene → Sonucu gözle → Çözümünü geliştir","Plan → Try → Observe → Improve"),color=Color(0xFF516078),modifier=Modifier.padding(vertical=12.dp),style=MaterialTheme.typography.bodySmall) }
    }
    if(!showReset && !showPrivacy && !showPerformance && !showExit)bannerContent()
    }
    if(showReset) AlertDialog(onDismissRequest={showReset=false},title={Text(label(language,"En baştan başlayalım mı?","Start from the beginning?"))},
        text={Text(label(language,"Türkçe ve İngilizce tüm atölye başarıları, puanlar, temel alıştırmalar, rotalar ve günlük bonuslar sıfırlanacak. Seviye 1'e döneceksin; yalnızca Görsel Algı açık kalacak.","All Turkish and English workshop achievements, scores, foundation exercises, routes and daily bonuses will reset. You will return to level 1 with only Visual Skills unlocked."))},
        confirmButton={TextButton(onClick={showReset=false;showPerformance=false;onResetProgress()},modifier=Modifier.testTag("confirm_reset_learning")) {Text(label(language,"Tüm ilerlemeyi sıfırla","Reset all progress"))}},
        dismissButton={TextButton(onClick={showReset=false}) {Text(label(language,"Vazgeç","Cancel"))}})
    if(showPrivacy) AlertDialog(onDismissRequest={showPrivacy=false},title={Text(label(language,"Ebeveynler için","For parents"))},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(label(language,"Hesap gerekmez. İlerleme bu cihazda saklanır. Tamamlanan atölyelerden sonra seyrek reklam gösterilebilir.","No account is required. Progress stays on this device. Occasional ads may appear after completed workshops."))
        Text(label(language,"Sesli okuma cihazdaki Türkçe veya İngilizce ses paketini kullanır. Paket yoksa kartlardaki yazılarla oynayabilirsin.","Narration uses an installed Turkish or English voice. If no voice is installed, play using the written cards."))
        Text(label(language,"Birlikte oynarken çocuğun çözümünü anlatmasına fırsat ver. Yanlış yanıtlar yeni bir deneme için bilgidir.","When playing together, give your child time to explain a solution. A wrong answer is information for the next attempt."))
    }},confirmButton={TextButton(onClick={showPrivacy=false}) {Text(label(language,"Anladım","Got it"))}})
    if (showPerformance) Dialog(
        onDismissRequest = { showPerformance = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.94f).heightIn(max = 720.dp),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(label(language, "Gelişim özeti", "Your progress"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        Text(label(language, "Her deneme yeni bir keşif!", "Every try is a new discovery!"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { showPerformance = false }, modifier = Modifier.testTag("close_progress")) {
                        Icon(Icons.Default.Close, contentDescription = label(language, "Kapat", "Close"))
                    }
                }

                Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(58.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text("🌟", fontSize = 32.sp) }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(label(language, "Keşif yolculuğun", "Discovery journey"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("$overallProgress%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        LinearProgressIndicator(progress = { overallProgress / 100f }, modifier = Modifier.fillMaxWidth().height(9.dp))
                        Text(label(language, "$completedMissions / $allMissions görev tamamlandı", "$completedMissions / $allMissions missions completed"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(label(language, "⭐ ${prefs.getInt("bonus_points", 0)} keşif puanı", "⭐ ${prefs.getInt("bonus_points", 0)} discovery points"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProgressStatCard(
                        icon = "🧩",
                        title = label(language, "Atölyeler", "Workshops"),
                        value = "$completedWorkshops / ${ChallengeCatalog.all.size}",
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        icon = "🌱",
                        title = label(language, "Seviyen", "Level"),
                        value = "$playerLevel",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(label(language, "Beceri yolculuğun", "Your skill journey"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                com.algokids.data.LearningPath.order.forEachIndexed { index, category ->
                    val unlocked = com.algokids.data.LearningPath.unlocked(prefs, category, language.name)
                    val percent = skillProgress[index]
                    if (unlocked || percent > 0) {
                        ProgressCategoryRow(categoryTitle(category, language), progressEmoji(category), percent)
                    }
                }

                if (performanceSummary.isNotEmpty()) {
                    Text(label(language, "Öne çıkanların", "Your highlights"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    performanceSummary.take(5).forEachIndexed { index, item ->
                        Surface(shape = RoundedCornerShape(16.dp), color = if (index % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondaryContainer) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(listOf("✨", "🎯", "⭐")[index % 3], fontSize = 20.sp)
                                Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(label(language, "İlk atölyeni tamamladığında yeni başarıların burada görünecek.", "Your first workshop will add a new achievement here."), Modifier.fillMaxWidth().padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Text(label(language, "Bu özet öğrenme yolculuğunu gösterir; bir zekâ testi değildir.", "This summary shows your learning journey. It is not an intelligence test."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    if(showExit) AlertDialog(onDismissRequest={showExit=false},title={Text(label(language,"Bugünlük ara verelim mi?","Take a break?"))},text={Text(label(language,"Kaydedilen ilerlemen seni bekleyecek.","Your saved progress will be waiting."))},confirmButton={TextButton(onClick={activity?.finish()}) {Text(label(language,"Çık","Exit"))}},dismissButton={TextButton(onClick={showExit=false}) {Text(label(language,"Devam et","Keep playing"))}})
}

@Composable
private fun ProgressStatCard(icon: String, title: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Text(icon, fontSize = 25.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun ProgressCategoryRow(title: String, icon: String, progress: Int) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = RoundedCornerShape(13.dp), color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.size(42.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 23.sp) }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("$progress%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp))
            }
        }
    }
}

private fun progressEmoji(category: GameCategory) = when (category) {
    GameCategory.VISUAL -> "🎨"
    GameCategory.NUMERICAL, GameCategory.NUMBERS -> "🔢"
    GameCategory.LOGIC -> "🧩"
    GameCategory.ATTENTION -> "🔎"
    GameCategory.MEMORY -> "🧠"
    GameCategory.AUDIOLOGY -> "🎵"
    GameCategory.GEOMETRY -> "🔷"
    GameCategory.ALPHABET -> "🔤"
    GameCategory.ALGORITHM -> "🧭"
}

@Composable
fun CategoryCard(category:CategoryItem,language:AppLanguage,enabled:Boolean=true,onClick:()->Unit) {
    val skill=when(category.type) {
        GameCategory.ALGORITHM -> label(language,"Rota · Döngü · Hata ayıklama","Routes · Loops · Debugging")
        GameCategory.NUMERICAL -> label(language,"İşlem zinciri · Terazi dengesi","Operation chains · Balance")
        GameCategory.LOGIC -> label(language,"Bağımlılıklar · İpucu eleme","Dependencies · Deduction")
        GameCategory.ATTENTION -> label(language,"Dur ve seç · Kural değişimi","Stop and select · Rule switching")
        GameCategory.MEMORY -> label(language,"Yer hafızası · Ters sıra","Locations · Reverse recall")
        GameCategory.VISUAL -> label(language,"Katmanlar · Eksik mozaik","Layers · Missing mosaic")
        GameCategory.AUDIOLOGY -> label(language,"Komutları dinle · Anlam çıkar","Listen to commands · Infer meaning")
        GameCategory.GEOMETRY -> label(language,"Zihinde döndür · Katla ve aç","Mental rotation · Reflection")
        GameCategory.ALPHABET -> label(language,"Harf sırası · Heceler","Letter order · Word parts")
        GameCategory.NUMBERS -> label(language,"Okunuş · Basamak değeri","Reading · Place value")
    }
    Card(onClick=onClick,enabled=enabled,shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(defaultElevation=3.dp),modifier=Modifier.fillMaxWidth().heightIn(min=180.dp)) {
        Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Surface(color=category.color.copy(alpha=.13f),shape=RoundedCornerShape(17.dp)) { Icon(category.icon,null,tint=category.color,modifier=Modifier.padding(11.dp).size(28.dp)) }
                Spacer(Modifier.weight(1f))
                Surface(color=category.color.copy(alpha=.1f),shape=CircleShape,modifier=Modifier.size(30.dp)) { Box(contentAlignment=Alignment.Center) { Icon(Icons.Default.ChevronRight,null,tint=category.color,modifier=Modifier.size(20.dp)) } }
            }
            Text(category.title,fontWeight=FontWeight.ExtraBold,color=Color(0xFF20334A),style=MaterialTheme.typography.titleMedium)
            Text(skill,color=Color(0xFF5D7180),style=MaterialTheme.typography.bodySmall,lineHeight=17.sp)
        }
    }
}

data class CategoryItem(val title:String,val icon:ImageVector,val color:Color,val type:GameCategory)

private data class HomeProgressSnapshot(
    val skillProgress: List<Int>,
    val allMissions: Int,
    val completedMissions: Int,
    val overallProgress: Int,
    val completedWorkshops: Int,
    val playerLevel: Int
)
