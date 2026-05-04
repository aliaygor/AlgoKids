@file:OptIn(ExperimentalMaterial3Api::class)

package com.algokids.ui.screens

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
    onStorySelect: (Story) -> Unit
) {
    var section by remember { mutableStateOf(HomeSection.GAMES) }
    var showPrivacy by remember { mutableStateOf(false) }

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
            pageImages = listOf("🐜", "🍎", "🍞", "😰", "🐜🐜🐜", "🏠", "😊", "💪", "💪")
        ),
        Story(
            id = "s2",
            title = "Cesur Tavşan",
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
            pageImages = listOf("🐰", "🧭", "🌈", "🌸", "🪨", "🏠", "🦊🐻🐰", "😊")
        ),
        Story(
            id = "s3",
            title = "Uzay Yolculuğu",
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
            pageImages = listOf("🚀", "🔥", "🌍", "👽", "✨", "⭐", "🌍", "🛌", "✨")
        ),
        Story(
            id = "s4",
            title = "Kayıp Renkler",
            content = "",
            pages = listOf(
                "Elif'in boya kutusunda bir sabah bütün renkler birbirine karışmıştı.",
                "Önce kırmızıyı elmaya, sarıyı güneşe, maviyi gökyüzüne ayırdı.",
                "Sonra renkleri sıraya dizip küçük bir gökkuşağı yaptı.",
                "Renkler doğru yerlerine dönünce resim defteri yeniden parladı.",
                "Elif her rengin kendi yerinde daha güzel göründüğünü öğrendi."
            ),
            pageImages = listOf("🎨", "🍎☀️🌌", "🌈", "📒", "😊")
        ),
        Story(
            id = "s5",
            title = "Robotun Planı",
            content = "",
            pages = listOf(
                "Mert küçük robotuna odasını toplamayı öğretmek istedi.",
                "Önce oyuncakları kutuya, kitapları rafa koyma kuralı yazdı.",
                "Robot bazen şaşırdı ama Mert adımları tek tek düzeltti.",
                "Sonunda robot sırayı öğrendi ve oda pırıl pırıl oldu.",
                "Mert iyi bir planın işleri kolaylaştırdığını fark etti."
            ),
            pageImages = listOf("🤖", "🧸📚", "🛠️", "✨", "🧠")
        ),
        Story(
            id = "s6",
            title = "Deniz Feneri",
            content = "",
            pages = listOf(
                "Minik kaptan Ada sisli bir akşam denizde yolunu arıyordu.",
                "Uzakta yanıp sönen deniz fenerini gördü.",
                "Işığı takip ederek kayalıklardan güvenle uzaklaştı.",
                "Limana vardığında fener bekçisine teşekkür etti.",
                "Ada, dikkatli bakmanın bazen en iyi pusula olduğunu öğrendi."
            ),
            pageImages = listOf("⛵", "💡", "🌊", "🏠", "🧭")
        ),
        Story(
            id = "s7",
            title = "Minik Mimar",
            content = "",
            pages = listOf(
                "Zeynep bloklarıyla sağlam bir köprü yapmak istiyordu.",
                "Önce iki büyük küpü yan yana koydu.",
                "Üstlerine uzun bir dikdörtgen yerleştirdi.",
                "Köprü sallanınca altına bir destek daha ekledi.",
                "Arabası köprüden geçince planının işe yaradığını gördü."
            ),
            pageImages = listOf("🏗️", "🎲🎲", "▭", "🧱", "🚗")
        ),
        Story(
            id = "s8",
            title = "Sessiz Kütüphane",
            content = "",
            pages = listOf(
                "Can kütüphanede en sevdiği kitabı arıyordu.",
                "Raflardaki renkleri takip etti: kırmızı, mavi, kırmızı, mavi.",
                "Sıradaki rafın mavi olması gerektiğini fark etti.",
                "Mavi rafta aradığı kitabı buldu.",
                "Örüntüleri görmek Can'ın işini kolaylaştırmıştı."
            ),
            pageImages = listOf("📚", "🔴🔵", "🔵", "📖", "😊")
        ),
        Story(
            id = "s9",
            title = "Yağmurdan Sonra",
            content = "",
            pages = listOf(
                "Yağmur durunca Ece bahçeye çıktı.",
                "Toprakta küçük ayak izleri gördü.",
                "İzleri takip edince bir salyangozun yaprağa tırmandığını fark etti.",
                "Ece yaprağı yolun kenarına taşıdı.",
                "Küçük canlılara dikkat etmek bahçeyi daha güvenli yaptı."
            ),
            pageImages = listOf("🌧️", "👣", "🍃", "🤲", "🌱")
        ),
        Story(
            id = "s10",
            title = "Kaybolan Melodi",
            content = "",
            pages = listOf(
                "Ada'nın müzik kutusu bir sabah aynı melodiyi çalmıyordu.",
                "Önce zil sesini, sonra kuş sesini, en son tren sesini dinledi.",
                "Sesleri doğru sıraya koyunca melodi geri geldi.",
                "Ada her sesi dikkatle dinlediğinde daha iyi hatırladığını anladı.",
                "Müzik kutusu yeniden neşeli neşeli çalmaya başladı."
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
                        text = label(language, "3 hata olursa başa dönersin.", "3 misses restart."),
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
                            StoryCard(story) { onStorySelect(story) }
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
fun StoryCard(story: Story, onClick: () -> Unit) {
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
            Text(story.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
