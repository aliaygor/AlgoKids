@file:OptIn(ExperimentalMaterial3Api::class)

package com.algokids.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

@Composable
fun HomeScreen(
    onCategorySelect: (GameCategory) -> Unit,
    onStorySelect: (Story) -> Unit
) {
    var showStories by remember { mutableStateOf(false) }

    val categories = listOf(
        CategoryItem("Görsel Algı", Icons.Default.Visibility, Color(0xFFFF7043), GameCategory.VISUAL),
        CategoryItem("Sayısal Mantık", Icons.Default.Calculate, Color(0xFF42A5F5), GameCategory.NUMERICAL),
        CategoryItem("Dikkat & Odak", Icons.Default.Psychology, Color(0xFF66BB6A), GameCategory.ATTENTION),
        CategoryItem("Mantık Yürütme", Icons.Default.Lightbulb, Color(0xFFFFA726), GameCategory.LOGIC),
        CategoryItem("Hafıza Gücü", Icons.Default.Memory, Color(0xFFAB47BC), GameCategory.MEMORY),
        CategoryItem("İşitsel Algı", Icons.Default.VolumeUp, Color(0xFF26A69A), GameCategory.AUDIOLOGY),
        CategoryItem("Geometri", Icons.Default.Category, Color(0xFF5C6BC0), GameCategory.GEOMETRY)
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
                "Birlikten kuvvet doğarmış!"
            ),
            pageImages = listOf("🐜", "🍎", "🍞", "😰", "🐜🐜🐜", "💪")
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
                "Orada yeni arkadaşlar edinmiş ve mutlu mesut oynamışlar."
            ),
            pageImages = listOf("🐰", "🧭", "🌈", "🌸", "🦊🐻🐰")
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
                "Ali uyandığında yastığının altında gümüş bir parıltı vardı."
            ),
            pageImages = listOf("🚀", "🔥", "🌍", "👽", "✨", "🛌")
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AlgoKids", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFF1F8E9))))
                .padding(padding)
        ) {
            Column {
                // Segmented Control (Simple)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { showStories = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!showStories) Color(0xFF4CAF50) else Color.LightGray),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    ) {
                        Text("Oyunlar")
                    }
                    Button(
                        onClick = { showStories = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (showStories) Color(0xFF4CAF50) else Color.LightGray),
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    ) {
                        Text("Hikayeler")
                    }
                }

                if (!showStories) {
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
                        items(sampleStories) { story ->
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
