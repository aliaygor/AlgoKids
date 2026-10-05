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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
    progressRevision:Int=0,onResetProgress:()->Unit={},onAppearance:()->Unit={}
) {
    var showPrivacy by remember { mutableStateOf(false) }
    var showPerformance by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val prefs = context.getSharedPreferences("algokids_progress",Context.MODE_PRIVATE)
    val recent = remember(progressRevision) { runCatching { GameCategory.valueOf(prefs.getString("recent_category", "") ?: "") }.getOrNull() }
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

    LazyVerticalGrid(columns=GridCells.Adaptive(156.dp),modifier=Modifier.fillMaxSize().background(Color(0xFFF4F6FC)).safeDrawingPadding(),contentPadding=PaddingValues(20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item(span={GridItemSpan(maxLineSpan)}) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("AlgoKids",fontWeight=FontWeight.ExtraBold,fontSize=28.sp,color=Color(0xFF172B4D))
                    Text(label(language,"Küçük adımlar, güçlü düşünceler","Small steps, thoughtful minds"),style=MaterialTheme.typography.bodySmall,color=Color(0xFF516078))
                }
                IconButton(onClick={showPerformance=true}) { Icon(Icons.Default.Assessment,label(language,"Gelişim özeti","Progress"),tint=Color(0xFF3D68B5)) }
                IconButton(onClick=onParentSettings) { Icon(Icons.Default.PrivacyTip,label(language,"Ebeveyn bilgisi","Parent information"),tint=Color(0xFF308263)) }
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {
            Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=if(prefs.getString("cosmetic_theme","ocean")=="sunset") Color(0xFF703C47) else Color(0xFF172B4D))) {
                Column(Modifier.fillMaxWidth().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text(label(language,"BUGÜNÜN DÜŞÜNME GÖREVİ","TODAY’S THINKING MISSION"),color=Color(0xFFA8DDD4),style=MaterialTheme.typography.labelMedium)
                    Text(label(language,"Yaşına uygun günlük keşif","A daily discovery for your age"),fontWeight=FontWeight.Bold,fontSize=26.sp,color=Color.White)
                    Text(label(language,"Seviye ${com.algokids.data.LearningPath.level(prefs,language.name)} · ${prefs.getInt("bonus_points",0)} keşif puanı","Level ${com.algokids.data.LearningPath.level(prefs,language.name)} · ${prefs.getInt("bonus_points",0)} discovery points"),color=Color(0xFFDEE6F5),style=MaterialTheme.typography.bodyLarge)
                    Button(onClick=onDailyMission,colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFA8DDD4),contentColor=Color(0xFF172B4D)),shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) { Text(label(language,"Günlük meydan okuma","Daily challenge"),fontWeight=FontWeight.Bold) }
                }
            }
        }
        if(recent!=null && com.algokids.data.LearningPath.unlocked(prefs,recent,language.name)) item(span={GridItemSpan(maxLineSpan)}) {
            OutlinedButton(onClick={onCategorySelect(recent)},modifier=Modifier.fillMaxWidth().heightIn(min=52.dp),shape=RoundedCornerShape(16.dp)) { Text(label(language,"Devam et: ","Continue: ")+categoryTitle(recent,language)) }
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
            Card(onClick={onStorySelect(story)},shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
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
            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                val number=com.algokids.data.LearningPath.order.indexOf(category.type)+1
                Text(label(language,"Set $number · ${if(!unlocked) "Kilitli" else if(completed) "Tamamlandı" else "Açık"}","Set $number · ${if(!unlocked) "Locked" else if(completed) "Completed" else "Unlocked"}"),style=MaterialTheme.typography.labelLarge)
                CategoryCard(category,language,unlocked) { onCategorySelect(category.type) }
                Text(if(unlocked) label(language,"Başarı: ${com.algokids.data.LearningPath.score(prefs,category.type,language.name)}/100","Score: ${com.algokids.data.LearningPath.score(prefs,category.type,language.name)}/100") else label(language,"Önce önceki eğitim setini tamamla.","Complete the preceding learning set first."),style=MaterialTheme.typography.bodySmall)
            }
        }
        item(span={GridItemSpan(maxLineSpan)}) {TextButton(onClick=onAppearance,modifier=Modifier.testTag("appearance_options")) {Text(label(language,"Görünüm seçenekleri","Appearance"))}}
        item(span={GridItemSpan(maxLineSpan)}) { Text(label(language,"Önce planla → Dene → Sonucu gözle → Çözümünü geliştir","Plan → Try → Observe → Improve"),color=Color(0xFF516078),modifier=Modifier.padding(vertical=12.dp),style=MaterialTheme.typography.bodySmall) }
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
    if(showPerformance) AlertDialog(onDismissRequest={showPerformance=false},title={Text(label(language,"Gelişim özeti","Progress"))},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Text(label(language,"Bu cihazda tamamlanan görevler. Bu özet bir zekâ testi değildir.","Completed tasks on this device. This summary is not an intelligence test."))
        if(performanceSummary.isEmpty()) Text(label(language,"İlk atölyeni tamamladığında ilerlemen burada görünecek.","Finish your first workshop to see progress here."))
        performanceSummary.forEach { Text(it) }
    }},confirmButton={TextButton(onClick={showPerformance=false}) {Text(label(language,"Tamam","OK"))}})
    if(showExit) AlertDialog(onDismissRequest={showExit=false},title={Text(label(language,"Bugünlük ara verelim mi?","Take a break?"))},text={Text(label(language,"Kaydedilen ilerlemen seni bekleyecek.","Your saved progress will be waiting."))},confirmButton={TextButton(onClick={activity?.finish()}) {Text(label(language,"Çık","Exit"))}},dismissButton={TextButton(onClick={showExit=false}) {Text(label(language,"Devam et","Keep playing"))}})
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
    Card(onClick=onClick,enabled=enabled,shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=Color.White),modifier=Modifier.fillMaxWidth().heightIn(min=180.dp)) {
        Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Surface(color=category.color.copy(alpha=.1f),shape=RoundedCornerShape(14.dp)) { Icon(category.icon,null,tint=category.color,modifier=Modifier.padding(12.dp).size(28.dp)) }
            Text(category.title,fontWeight=FontWeight.Bold,color=Color(0xFF172B4D),style=MaterialTheme.typography.titleMedium)
            Text(skill,color=Color(0xFF516078),style=MaterialTheme.typography.bodySmall)
        }
    }
}

data class CategoryItem(val title:String,val icon:ImageVector,val color:Color,val type:GameCategory)
