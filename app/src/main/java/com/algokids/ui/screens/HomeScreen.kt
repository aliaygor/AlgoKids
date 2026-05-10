@file:OptIn(ExperimentalMaterial3Api::class)

package com.algokids.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.model.GameCategory
import com.algokids.game.model.Story

private enum class HomeSection {
    GAMES, STORIES
}

@Composable
fun HomeScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onCategorySelect: (GameCategory) -> Unit,
    onStorySelect: (Story) -> Unit,
    performanceSummary: List<String> = emptyList()
) {
    var section by remember { mutableStateOf(HomeSection.GAMES) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showPerformance by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler {
        if (section == HomeSection.STORIES) section = HomeSection.GAMES else showExitConfirm = true
    }

    val categories = listOf(
        CategoryItem(label(language, "Görsel Algı", "Visual Skills"), Icons.Default.Visibility, Color(0xFFFF7043), GameCategory.VISUAL),
        CategoryItem(label(language, "Sayısal Mantık", "Numbers"), Icons.Default.Calculate, Color(0xFF42A5F5), GameCategory.NUMERICAL),
        CategoryItem(label(language, "Dikkat & Odak", "Attention"), Icons.Default.Psychology, Color(0xFF66BB6A), GameCategory.ATTENTION),
        CategoryItem(label(language, "Mantık Yürütme", "Logic"), Icons.Default.Lightbulb, Color(0xFFFFA726), GameCategory.LOGIC),
        CategoryItem(label(language, "Algoritma", "Algorithm"), Icons.Default.AccountTree, Color(0xFF26C6DA), GameCategory.ALGORITHM),
        CategoryItem(label(language, "Hafıza Gücü", "Memory"), Icons.Default.Memory, Color(0xFFAB47BC), GameCategory.MEMORY),
        CategoryItem(label(language, "İşitsel Algı", "Listening"), Icons.Default.VolumeUp, Color(0xFF26A69A), GameCategory.AUDIOLOGY),
        CategoryItem(label(language, "Geometri", "Geometry"), Icons.Default.Category, Color(0xFF5C6BC0), GameCategory.GEOMETRY)
    )

    val sampleStories = listOf(
        Story(
            id = "s1",
            title = "Küçük Karınca",
            titleEn = "Little Ant",
            content = "",
            pages = listOf(
                "Bir zamanlar çok çalışkan küçük bir karınca varmış.",
                "Karınca bütün gün yuvasına yemek taşırmış.",
                "Bir gün yolda büyük bir ekmek kırıntısı bulmuş.",
                "Kırıntı o kadar büyükmüş ki, karınca onu tek başına taşıyamamış.",
                "Hemen arkadaşlarını çağırmış ve hep beraber kırıntıyı yuvaya götürmüşler.",
                "Yuvaya vardıklarında herkes sırayla dinlenmiş.",
                "Küçük karınca arkadaşlarına teşekkür etmiş.",
                "O günden sonra büyük işleri hep birlikte yapmışlar.",
                "Birlikten kuvvet doğarmış!"
            ),
            pagesEn = listOf(
                "Once there was a tiny ant who worked very hard.",
                "All day, the ant carried food to her home.",
                "One day she found a big crumb of bread.",
                "The crumb was too big to carry alone.",
                "She called her friends, and together they carried it home.",
                "When they arrived, everyone took a little rest.",
                "The tiny ant thanked her friends.",
                "After that day, they did big jobs together.",
                "Working together makes us strong!"
            ),
            pageImages = listOf("🐜", "🍎", "🍞", "😰", "🐜🐜🐜", "🏠", "😊", "💪", "💪")
        ),
        Story(
            id = "s2",
            title = "Cesur Tavşan",
            titleEn = "Brave Bunny",
            content = "",
            pages = listOf(
                "Ormanın derinliklerinde Cesur adında minik bir tavşan yaşarmış.",
                "Cesur, diğer tavşanların aksine yeni yerler keşfetmeyi çok severmiş.",
                "Bir sabah gökkuşağının bittiği yeri bulmaya karar vermiş.",
                "Dereyi geçmiş, tepeleri tırmanmış ve rengarenk bir çiçek bahçesine varmış.",
                "Bahçede kaybolmamak için yoluna küçük taşlar bırakmış.",
                "Akşam olunca taşları takip ederek evine dönmüş.",
                "Orada yeni arkadaşlar edinmiş ve ertesi gün onları yuvasına çağırmış.",
                "Cesur, keşfetmenin güzel olduğunu ama dikkatli olmanın da önemli olduğunu öğrenmiş."
            ),
            pagesEn = listOf(
                "Deep in the forest lived a little bunny named Brave.",
                "Brave loved to discover new places.",
                "One morning he decided to find where the rainbow ended.",
                "He crossed a stream, climbed hills, and found a colorful flower garden.",
                "He dropped little stones so he would not get lost.",
                "In the evening, he followed the stones back home.",
                "He met new friends and invited them to his burrow.",
                "Brave learned that exploring is fun, and being careful matters too."
            ),
            pageImages = listOf("🐰", "🧭", "🌈", "🌸", "🪨", "🏠", "🦊🐻🐰", "😊")
        ),
        Story(
            id = "s3",
            title = "Uzay Yolculuğu",
            titleEn = "Space Trip",
            content = "",
            pages = listOf(
                "Ali, bir gece rüyasında dev bir rokete bindiğini gördü.",
                "Roket büyük bir gürültüyle gökyüzüne fırladı.",
                "Pencereden baktığında Dünya'nın küçüldüğünü gördü.",
                "Ay'a indiğinde orada zıplayan komik uzaylılarla karşılaştı.",
                "Uzaylılar ona yıldız tozu hediye ettiler.",
                "Ali yıldızları saydı ve en parlak olanı seçti.",
                "Roket eve dönerken Dünya yavaş yavaş büyüdü.",
                "Ali uyandığında yastığının altında gümüş bir parıltı vardı.",
                "O parıltı ona rüyasını hatırlattı."
            ),
            pagesEn = listOf(
                "One night, Ali dreamed he climbed into a giant rocket.",
                "The rocket flew into the sky with a loud whoosh.",
                "From the window, Earth looked smaller and smaller.",
                "On the Moon, he met funny jumping aliens.",
                "The aliens gave him a little star dust.",
                "Ali counted the stars and chose the brightest one.",
                "As the rocket came home, Earth grew bigger again.",
                "When Ali woke up, a silver sparkle was under his pillow.",
                "The sparkle reminded him of his dream."
            ),
            pageImages = listOf("🚀", "🔥", "🌍", "👽", "✨", "⭐", "🌍", "🛌", "✨")
        ),
        Story(
            id = "s4",
            title = "Kayıp Renkler",
            titleEn = "The Lost Colors",
            content = "",
            pages = listOf(
                "Elif'in boya kutusunda bir sabah bütün renkler birbirine karışmıştı.",
                "Önce kırmızıyı elmaya, sarıyı güneşe, maviyi gökyüzüne ayırdı.",
                "Sonra renkleri sıraya dizip küçük bir gökkuşağı yaptı.",
                "Renkler doğru yerlerine dönünce resim defteri yeniden parladı.",
                "Elif her rengin kendi yerinde daha güzel göründüğünü öğrendi."
            ),
            pagesEn = listOf(
                "One morning, all the colors in Elif's paint box were mixed up.",
                "She put red with the apple, yellow with the sun, and blue with the sky.",
                "Then she lined the colors up and made a small rainbow.",
                "When every color found its place, her notebook shined again.",
                "Elif learned that each color looks lovely in the right place."
            ),
            pageImages = listOf("🎨", "🍎☀️🌌", "🌈", "📒", "😊")
        ),
        Story(
            id = "s5",
            title = "Robotun Planı",
            titleEn = "Robot's Plan",
            content = "",
            pages = listOf(
                "Mert küçük robotuna odasını toplamayı öğretmek istedi.",
                "Önce oyuncakları kutuya, kitapları rafa koyma kuralı yazdı.",
                "Robot bazen şaşırdı ama Mert adımları tek tek düzeltti.",
                "Sonunda robot sırayı öğrendi ve oda pırıl pırıl oldu.",
                "Mert iyi bir planın işleri kolaylaştırdığını fark etti."
            ),
            pagesEn = listOf(
                "Mert wanted to teach his little robot to tidy the room.",
                "First he wrote a rule: toys go in the box, books go on the shelf.",
                "The robot got confused sometimes, but Mert fixed the steps one by one.",
                "At last, the robot learned the order and the room became shiny clean.",
                "Mert saw that a good plan makes work easier."
            ),
            pageImages = listOf("🤖", "🧸📚", "🛠️", "✨", "🧠")
        ),
        Story(
            id = "s6",
            title = "Deniz Feneri",
            titleEn = "The Lighthouse",
            content = "",
            pages = listOf(
                "Minik kaptan Ada sisli bir akşam denizde yolunu arıyordu.",
                "Uzakta yanıp sönen deniz fenerini gördü.",
                "Işığı takip ederek kayalıklardan güvenle uzaklaştı.",
                "Limana vardığında fener bekçisine teşekkür etti.",
                "Ada, dikkatli bakmanın bazen en iyi pusula olduğunu öğrendi."
            ),
            pagesEn = listOf(
                "Little captain Ada was looking for her way on a foggy evening.",
                "Far away, she saw the lighthouse blinking.",
                "She followed the light and stayed safely away from the rocks.",
                "When she reached the harbor, she thanked the lighthouse keeper.",
                "Ada learned that looking carefully can be the best compass."
            ),
            pageImages = listOf("⛵", "💡", "🌊", "🏠", "🧭")
        ),
        Story(
            id = "s7",
            title = "Minik Mimar",
            titleEn = "Little Builder",
            content = "",
            pages = listOf(
                "Zeynep bloklarıyla sağlam bir köprü yapmak istiyordu.",
                "Önce iki büyük küpü yan yana koydu.",
                "Üstlerine uzun bir dikdörtgen yerleştirdi.",
                "Köprü sallanınca altına bir destek daha ekledi.",
                "Arabası köprüden geçince planının işe yaradığını gördü."
            ),
            pagesEn = listOf(
                "Zeynep wanted to build a strong bridge with her blocks.",
                "First she placed two big cubes side by side.",
                "Then she put a long rectangle on top.",
                "When the bridge wobbled, she added one more support.",
                "When her car crossed the bridge, she saw her plan worked."
            ),
            pageImages = listOf("🏗️", "🎲🎲", "▭", "🧱", "🚗")
        ),
        Story(
            id = "s8",
            title = "Sessiz Kütüphane",
            titleEn = "Quiet Library",
            content = "",
            pages = listOf(
                "Can kütüphanede en sevdiği kitabı arıyordu.",
                "Raflardaki renkleri takip etti: kırmızı, mavi, kırmızı, mavi.",
                "Sıradaki rafın mavi olması gerektiğini fark etti.",
                "Mavi rafta aradığı kitabı buldu.",
                "Örüntüleri görmek Can'ın işini kolaylaştırmıştı."
            ),
            pagesEn = listOf(
                "Can was looking for his favorite book in the library.",
                "He followed the shelf colors: red, blue, red, blue.",
                "He noticed the next shelf should be blue.",
                "On the blue shelf, he found the book he wanted.",
                "Seeing the pattern made Can's job easier."
            ),
            pageImages = listOf("📚", "🔴🔵", "🔵", "📖", "😊")
        ),
        Story(
            id = "s9",
            title = "Yağmurdan Sonra",
            titleEn = "After the Rain",
            content = "",
            pages = listOf(
                "Yağmur durunca Ece bahçeye çıktı.",
                "Toprakta küçük ayak izleri gördü.",
                "İzleri takip edince bir salyangozun yaprağa tırmandığını fark etti.",
                "Ece yaprağı yolun kenarına taşıdı.",
                "Küçük canlılara dikkat etmek bahçeyi daha güvenli yaptı."
            ),
            pagesEn = listOf(
                "When the rain stopped, Ece went into the garden.",
                "She saw tiny footprints in the soil.",
                "She followed them and found a snail climbing a leaf.",
                "Ece moved the leaf to the side of the path.",
                "Taking care of little creatures made the garden safer."
            ),
            pageImages = listOf("🌧️", "👣", "🍃", "🤲", "🌱")
        ),
        Story(
            id = "s10",
            title = "Kaybolan Melodi",
            titleEn = "The Missing Melody",
            content = "",
            pages = listOf(
                "Ada'nın müzik kutusu bir sabah aynı melodiyi çalmıyordu.",
                "Önce zil sesini, sonra kuş sesini, en son tren sesini dinledi.",
                "Sesleri doğru sıraya koyunca melodi geri geldi.",
                "Ada her sesi dikkatle dinlediğinde daha iyi hatırladığını anladı.",
                "Müzik kutusu yeniden neşeli neşeli çalmaya başladı."
            ),
            pagesEn = listOf(
                "Ada's music box did not play the same melody one morning.",
                "First she heard a bell, then a bird, and last a train.",
                "When she put the sounds in the right order, the melody came back.",
                "Ada learned that listening carefully helps her remember.",
                "The music box played happily again."
            ),
            pageImages = listOf("🎵", "🔔🐦🚂", "🎼", "👂", "🎶")
        )
    )
    val displayedStories = when (section) {
        HomeSection.STORIES -> sampleStories
        HomeSection.GAMES -> emptyList()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AlgoKids", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp) },
                actions = {
                    IconButton(onClick = { showPerformance = true }) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF1976D2))
                    }
                    IconButton(onClick = { showPrivacy = true }) {
                        Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = Color(0xFF2E7D32))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { padding ->
        if (showPrivacy) {
            AlertDialog(
                onDismissRequest = { showPrivacy = false },
                title = { Text(label(language, "Gizlilik", "Privacy")) },
                text = {
                    Text(
                        label(
                            language,
                            "AlgoKids hesap istemez, reklam göstermez, internet kullanmaz. Kamera, mikrofon, konum ve kişi bilgisi istemez. Oyun ilerlemesi sadece cihaz içinde tutulur.",
                            "AlgoKids does not require accounts, show ads, or use internet. It does not request camera, microphone, location, or contacts. Game progress stays on the device."
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacy = false }) {
                        Text(label(language, "Tamam", "OK"))
                    }
                }
            )
        }
        if (showPerformance) {
            AlertDialog(
                onDismissRequest = { showPerformance = false },
                title = { Text(label(language, "Oyun Geçmişi", "Progress")) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (performanceSummary.isEmpty()) {
                            Text(label(language, "Henüz oyun oynanmadı.", "No games played yet."))
                        } else {
                            performanceSummary.forEach { item ->
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(item, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPerformance = false }) {
                        Text(label(language, "Tamam", "OK"))
                    }
                }
            )
        }
        if (showExitConfirm) {
            AlertDialog(
                onDismissRequest = { showExitConfirm = false },
                title = { Text(label(language, "Çıkılsın mı?", "Exit?")) },
                text = { Text(label(language, "Oyundan çıkmak ister misin?", "Do you want to exit?")) },
                confirmButton = {
                    Button(onClick = { activity?.finish() }) { Text(label(language, "Çık", "Exit")) }
                },
                dismissButton = {
                    TextButton(onClick = { showExitConfirm = false }) { Text(label(language, "Kal", "Stay")) }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFF1F8E9))))
                .padding(padding)
        ) {
            Column {
                // Segmented Control (Simple)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { section = HomeSection.GAMES },
                        colors = ButtonDefaults.buttonColors(containerColor = if (section == HomeSection.GAMES) Color(0xFF4CAF50) else Color.LightGray),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    ) {
                        Text(label(language, "Oyunlar", "Games"))
                    }
                    Button(
                        onClick = { section = HomeSection.STORIES },
                        colors = ButtonDefaults.buttonColors(containerColor = if (section == HomeSection.STORIES) Color(0xFF4CAF50) else Color.LightGray),
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    ) {
                        Text(label(language, "Hikayeler", "Stories"))
                    }

                    Spacer(Modifier.width(12.dp))

                    AssistChip(
                        onClick = { onLanguageChange(if (language == AppLanguage.TR) AppLanguage.EN else AppLanguage.TR) },
                        label = { Text(if (language == AppLanguage.TR) "TR" else "EN") },
                        leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                if (section == HomeSection.GAMES) {
                    Text(
                        text = label(language, "Yanlış yapınca tekrar deneyebilirsin.", "Try again when you miss."),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFF546E7A),
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = label(language, "Sayfa değişince okur.", "Reads each page."),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFF546E7A),
                        fontSize = 13.sp
                    )
                }

                if (section == HomeSection.GAMES) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(categories) { category ->
                            CategoryCard(category) { onCategorySelect(category.type) }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(displayedStories) { story ->
                            StoryCard(story, language) { onStorySelect(story) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(category.color.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = category.color)
            }
            Spacer(Modifier.height(12.dp))
            Text(category.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF37474F))
        }
    }
}

@Composable
fun StoryCard(story: Story, language: AppLanguage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoStories, null, modifier = Modifier.size(40.dp), tint = Color(0xFF8BC34A))
            Spacer(Modifier.width(16.dp))
            Text(
                label(language, story.title, story.titleEn),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }
    }
}

data class CategoryItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val type: GameCategory
)
