package com.algokids.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.game.model.GameCategory
import com.algokids.game.model.GameContent
import com.algokids.game.model.GameType
import com.algokids.ui.components.GameScene
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun GameScreen(
    items: List<GameContent>,
    onBack: () -> Unit
) {
    if (items.isEmpty()) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val sortedItems = remember(items) { 
        items.sortedBy { it.id?.filter { char -> char.isDigit() }?.toIntOrNull() ?: 0 } 
    }
    
    val context = LocalContext.current
    val tts = remember { TextToSpeech(context, null) }
    var index by remember { mutableIntStateOf(0) }
    val currentContent = sortedItems.getOrNull(index) ?: sortedItems[0]
    var isCorrect by remember(index) { mutableStateOf(false) }

    var selectedLeft by remember(index) { mutableStateOf<String?>(null) }
    var matchedPairs by remember(index) { mutableStateOf(setOf<String>()) }
    
    var showBackDialog by remember { mutableStateOf(false) }
    
    var isHidden by remember(index) { mutableStateOf(false) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    
    LaunchedEffect(currentContent) {
        tts.language = Locale("tr")
        if (isSoundEnabled) {
            tts.speak(currentContent.instruction ?: "", TextToSpeech.QUEUE_FLUSH, null, null)
        }
        
        if (currentContent.category == GameCategory.MEMORY) {
            isHidden = false
            delay(2500) 
            isHidden = true
        }
    }

    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("Oyundan Çıkıyor Musun?") },
            text = { Text("Eğer çıkarsan ilerlemen kaybolabilir. Devam etmek istiyor musun?") },
            confirmButton = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Evet, Çık")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackDialog = false }) {
                    Text("Hayır, Devam Et")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    GameScene(
        title = when(currentContent.category) {
            GameCategory.MEMORY -> "Hafıza Gücü"
            GameCategory.AUDIOLOGY -> "Kulak Misafiri"
            GameCategory.GEOMETRY -> "Şekil Avcısı"
            else -> when(currentContent.type) {
                GameType.PATTERN -> "Örüntü Zamanı"
                GameType.COUNTING -> "Sayıları Sayalım"
                GameType.SHADOW_MATCH -> "Gölgeyi Bul"
                GameType.WHICH_IS_DIFFERENT, GameType.ODD_ONE_OUT -> "Farklı Olanı Bul"
                GameType.MATCHING -> "Eşini Bul"
                GameType.SIZE_COMPARISON -> "Büyük - Küçük"
                GameType.FUNCTIONAL_MATCH -> "Neyle Kullanılır?"
                GameType.SAME_OBJECT -> "Aynısını Bul"
                GameType.FIND_THE_PAIR -> "Çiftini Bul"
                GameType.SEQUENCE_LOGIC, GameType.WHAT_IS_NEXT -> "Mantık Sırası"
                else -> "Eğlence Vakti"
            }
        },
        instruction = currentContent.instruction ?: "",
        progress = (index + 1).toFloat() / sortedItems.size,
        onBack = { if (index > 0) index-- else showBackDialog = true },
        onExit = { showBackDialog = true },
        isSoundEnabled = isSoundEnabled,
        onToggleSound = { isSoundEnabled = !isSoundEnabled },
        onSound = { tts.speak(currentContent.instruction ?: "", TextToSpeech.QUEUE_FLUSH, null, null) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = currentContent, label = "") { content ->
                    when {
                        content.category == GameCategory.AUDIOLOGY -> AudiologyView()
                        content.category == GameCategory.MEMORY -> MemoryView(content, isHidden)
                        content.type == GameType.PATTERN || content.type == GameType.WHAT_IS_NEXT -> PatternGameView(content, isCorrect)
                        content.type == GameType.COUNTING -> CountingGameView(content)
                        content.type == GameType.SHADOW_MATCH -> ShadowGameView(content)
                        content.type == GameType.WHICH_IS_DIFFERENT || content.type == GameType.ODD_ONE_OUT -> DifferentGameView(content)
                        content.type == GameType.MATCHING || content.type == GameType.FUNCTIONAL_MATCH -> MatchingGameView(
                            content = content,
                            selectedLeft = selectedLeft,
                            matchedPairs = matchedPairs,
                            onLeftSelect = { selectedLeft = it },
                            onRightSelect = { right ->
                                if (selectedLeft != null) {
                                    if (checkMatch(content, selectedLeft!!, right)) {
                                        matchedPairs = matchedPairs + selectedLeft!! + right
                                        selectedLeft = null
                                        
                                        val allLeftMatched = content.questionAssets?.all { matchedPairs.contains(it) } ?: true
                                        val allRightMatched = content.options?.all { matchedPairs.contains(it) } ?: true
                                        
                                        if (allLeftMatched && allRightMatched) {
                                            isCorrect = true
                                            if (isSoundEnabled) {
                                                tts.speak("Harika! Hepsini buldun.", TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        } else {
                                            if (isSoundEnabled) {
                                                tts.speak("Aferin!", TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    } else {
                                        if (isSoundEnabled) {
                                            tts.speak("Bu onunla eşleşmiyor.", TextToSpeech.QUEUE_FLUSH, null, null)
                                        }
                                        selectedLeft = null
                                    }
                                }
                            }
                        )
                        content.type == GameType.SIZE_COMPARISON -> SizeComparisonView(content)
                        content.type == GameType.SAME_OBJECT || content.type == GameType.FIND_THE_PAIR -> SameObjectView(content)
                        else -> Text("Çok yakında!")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isCorrect) {
                    val isMatchingType = currentContent.type == GameType.MATCHING || currentContent.type == GameType.FUNCTIONAL_MATCH
                    if (!isMatchingType) {
                        val shuffledOptions = remember(currentContent) { currentContent.options?.shuffled() ?: emptyList() }
                        OptionsGrid(shuffledOptions) { selected ->
                            if (selected.equals(currentContent.answer, ignoreCase = true)) {
                                isCorrect = true
                                if (isSoundEnabled) {
                                    tts.speak("Harika! Doğru cevap.", TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            } else {
                                if (isSoundEnabled) {
                                    tts.speak("Bir daha dene bakalım.", TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (index < sortedItems.size - 1) index++ else onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("SÜPER! SONRAKİNE GEÇ ➔", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryView(content: GameContent, isHidden: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (isHidden) "Nesne hangisiydi?" else "Dikkatle bak!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHidden) Color(0xFF42A5F5) else Color(0xFFFF7043)
        )
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFEEEEEE), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!isHidden) {
                AssetIcon(content.questionAssets?.firstOrNull() ?: "", size = 90.dp)
            } else {
                Icon(Icons.Default.QuestionMark, null, modifier = Modifier.size(60.dp), tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun AudiologyView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color(0xFFE1F5FE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.VolumeUp, 
                contentDescription = null, 
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF0288D1)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("Sesi Dinle!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

fun checkMatch(content: GameContent, left: String, right: String): Boolean {
    val functionalMap = mapOf(
        "apple" to "tree", "elma" to "ağaç",
        "bee" to "flower", "arı" to "çiçek",
        "dog" to "bone", "köpek" to "kemik",
        "bird" to "nest", "kuş" to "yuva",
        "rain" to "umbrella", "yağmur" to "şemsiye",
        "sun" to "sunglasses", "güneş" to "gözlük",
        "monkey" to "banana", "maymun" to "muz",
        "rabbit" to "carrot", "tavşan" to "havuç",
        "car" to "tire", "araba" to "tekerlek",
        "hammer" to "nail", "çekiç" to "çivi",
        "fish" to "sea", "balık" to "deniz",
        "house" to "dog", "ev" to "köpek",
        "cow" to "milk", "inek" to "süt",
        "chicken" to "egg", "tavuk" to "yumurta",
        "pencil" to "paper", "kalem" to "kağıt",
        "brush" to "paint", "fırça" to "boya"
    )

    // Geometri Kenar Sayıları Eşleştirmesi
    val geometryEdgeMap = mapOf(
        "TRIANGLE" to "3", "ÜÇGEN" to "3",
        "SQUARE" to "4", "KARE" to "4",
        "RECTANGLE" to "4", "DİKDÖRTGEN" to "4",
        "CIRCLE" to "0", "DAİRE" to "0",
        "STAR_SHAPE" to "5", "YILDIZ" to "5",
        "HEART" to "KALP"
    )
    
    return when(content.type) {
        GameType.FUNCTIONAL_MATCH -> {
            if (content.category == GameCategory.GEOMETRY) {
                (geometryEdgeMap[left.uppercase()] == right.uppercase()) ||
                (geometryEdgeMap[right.uppercase()] == left.uppercase())
            } else {
                (functionalMap[left.lowercase()]?.equals(right, ignoreCase = true) == true) || 
                (functionalMap[right.lowercase()]?.equals(left, ignoreCase = true) == true)
            }
        }
        else -> left.equals(right, ignoreCase = true)
    }
}

@Composable
fun SizeComparisonView(content: GameContent) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        content.questionAssets?.forEach { asset ->
            val size = when(asset.lowercase()) {
                "elephant", "fil", "big", "büyük" -> 140.dp
                "cat", "kedi", "medium", "orta" -> 80.dp
                "ant", "karınca", "small", "küçük" -> 40.dp
                "giraffe", "zürafa" -> 150.dp
                "lion", "aslan" -> 110.dp
                "rabbit", "tavşan" -> 60.dp
                else -> 80.dp
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                AssetIcon(asset, size = size)
            }
        }
    }
}

@Composable
fun SameObjectView(content: GameContent) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val instructionText = when(content.type) {
            GameType.FIND_THE_PAIR -> "Bunun çiftini bulabilir misin?"
            else -> content.instruction ?: "Bu nesnenin aynısını bul:"
        }
        Text(instructionText, fontSize = 18.sp, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFF1F8E9), RoundedCornerShape(24.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AssetIcon(content.questionAssets?.firstOrNull() ?: "", size = 80.dp)
        }
    }
}

@Composable
fun MatchingGameView(
    content: GameContent,
    selectedLeft: String?,
    matchedPairs: Set<String>,
    onLeftSelect: (String) -> Unit,
    onRightSelect: (String) -> Unit
) {
    val leftItems = remember(content) { content.questionAssets ?: emptyList() }
    val rightItems = remember(content) {
        val originalOptions = content.options ?: emptyList()
        var shuffled = originalOptions.shuffled()
        
        if ((content.type == GameType.MATCHING || content.type == GameType.FUNCTIONAL_MATCH) && leftItems.size == shuffled.size) {
            var attempt = 0
            while (attempt < 10 && leftItems.indices.any { checkMatch(content, leftItems[it], shuffled[it]) }) {
                shuffled = originalOptions.shuffled()
                attempt++
            }
        }
        shuffled
    }

    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            leftItems.forEach { item ->
                val isMatched = matchedPairs.contains(item)
                val isSelected = selectedLeft == item
                MatchingCard(item, isMatched, isSelected) { onLeftSelect(item) }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rightItems.forEach { item ->
                val isMatched = matchedPairs.contains(item)
                MatchingCard(item, isMatched, false) { onRightSelect(item) }
            }
        }
    }
}

@Composable
fun MatchingCard(name: String, isMatched: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(65.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp))
            .background(
                if (isMatched) Color(0xFFE8F5E9) else if (isSelected) Color(0xFFFFF9C4) else Color.White,
                RoundedCornerShape(16.dp)
            )
            .border(
                2.dp, 
                if (isMatched) Color(0xFF4CAF50) else if (isSelected) Color(0xFFFBC02D) else Color.Transparent, 
                RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isMatched) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AssetIcon(name, size = 40.dp)
        if (isMatched) {
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(18.dp))
        }
    }
}

@Composable
fun OptionsGrid(options: List<String>, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEach { option ->
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onSelected(option) }
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AssetIcon(option, size = 45.dp)
                }
            }
        }
    }
}

@Composable
fun PatternGameView(content: GameContent, isCorrect: Boolean) {
    val scrollState = rememberScrollState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        val sequence = content.sequence ?: content.questionAssets ?: emptyList()
        sequence.forEach { item ->
            AssetIcon(item, size = 45.dp)
            Spacer(Modifier.width(12.dp))
        }
        
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isCorrect) Color.Transparent else Color(0xFFF5F5F5))
                .border(3.dp, if (isCorrect) Color(0xFF4CAF50) else Color(0xFFBDBDBD), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isCorrect) AssetIcon(content.answer ?: "", size = 50.dp)
            else Text("?", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
        }
    }
}

@Composable
fun CountingGameView(content: GameContent) {
    val assets = content.questionAssets ?: emptyList()
    val columns = if (assets.size > 6) 4 else 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(assets) { asset ->
            AssetIcon(asset, size = if (assets.size > 9) 50.dp else 70.dp)
        }
    }
}

@Composable
fun ShadowGameView(content: GameContent) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Bu hangi şekil?", fontSize = 18.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color(0xFFE0E0E0), CircleShape)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            val target = content.questionAssets?.firstOrNull()?.replace("shadow_", "") ?: content.answer ?: ""
            AssetIcon(target, size = 100.dp, isShadow = true)
        }
    }
}

@Composable
fun DifferentGameView(content: GameContent) {
    val assets = content.questionAssets ?: emptyList()
    val columns = if (assets.size > 4) 3 else assets.size
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(assets) { asset ->
            AssetIcon(asset, size = 65.dp)
        }
    }
}

@Composable
fun AssetIcon(name: String, size: Dp = 50.dp, isShadow: Boolean = false) {
    val cleanName = name.uppercase().replace("SHADOW_", "")
    
    val colorMap = mapOf(
        "RED" to Color(0xFFFF5252), "KIRMIZI" to Color(0xFFFF5252),
        "BLUE" to Color(0xFF448AFF), "MAVI" to Color(0xFF448AFF),
        "GREEN" to Color(0xFF4CAF50), "YEŞİL" to Color(0xFF4CAF50),
        "YELLOW" to Color(0xFFFFEB3B), "SARI" to Color(0xFFFFEB3B),
        "ORANGE_COLOR" to Color(0xFFFF9800), "TURUNCU" to Color(0xFFFF9800),
        "PURPLE" to Color(0xFF9C27B0), "MOR" to Color(0xFF9C27B0),
        "PINK" to Color(0xFFFF4081), "PEMBE" to Color(0xFFFF4081),
        "BROWN" to Color(0xFF795548), "KAHVERENGİ" to Color(0xFF795548)
    )

    // Geometri Çizimleri
    val shapes = listOf("CIRCLE", "DAIRE", "SQUARE", "KARE", "TRIANGLE", "UCGEN", "ÜÇGEN", "RECTANGLE", "DIKDORTGEN", "STAR_SHAPE", "HEART")
    if (shapes.contains(cleanName)) {
        Box(modifier = Modifier.size(size).padding(4.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val color = if (isShadow) Color.Black else Color(0xFF3F51B5)
                when (cleanName) {
                    "CIRCLE", "DAIRE" -> drawCircle(color)
                    "SQUARE", "KARE" -> drawRect(color)
                    "TRIANGLE", "UCGEN", "ÜÇGEN" -> {
                        val path = Path().apply {
                            moveTo(this@Canvas.size.width / 2f, 0f)
                            lineTo(this@Canvas.size.width, this@Canvas.size.height)
                            lineTo(0f, this@Canvas.size.height)
                            close()
                        }
                        drawPath(path, color)
                    }
                    "RECTANGLE", "DIKDORTGEN" -> drawRect(color, size = Size(this.size.width, this.size.height * 0.7f), topLeft = Offset(0f, this.size.height * 0.15f))
                    "STAR_SHAPE" -> {
                        val path = Path().apply {
                            val centerX = this@Canvas.size.width / 2f
                            val centerY = this@Canvas.size.height / 2f
                            val outerRadius = this@Canvas.size.width / 2f
                            val innerRadius = outerRadius / 2.5f
                            for (i in 0 until 10) {
                                val radius = if (i % 2 == 0) outerRadius else innerRadius
                                val angle = Math.toRadians(i * 36.0 - 90.0)
                                val x = centerX + radius * Math.cos(angle).toFloat()
                                val y = centerY + radius * Math.sin(angle).toFloat()
                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                            }
                            close()
                        }
                        drawPath(path, color)
                    }
                    "HEART" -> {
                        val path = Path().apply {
                            val width = this@Canvas.size.width
                            val height = this@Canvas.size.height
                            moveTo(width / 2f, height / 4f)
                            cubicTo(width / 4f, 0f, 0f, height / 4f, 0f, height / 2f)
                            cubicTo(0f, height * 0.75f, width / 2f, height, width / 2f, height)
                            cubicTo(width / 2f, height, width, height * 0.75f, width, height / 2f)
                            cubicTo(width, height / 4f, width * 0.75f, 0f, width / 2f, height / 4f)
                        }
                        drawPath(path, color)
                    }
                }
            }
        }
        return
    }

    val color = colorMap[cleanName]
    if (color != null) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(if (isShadow) 0.dp else 4.dp, CircleShape)
                .background(
                    brush = if (isShadow) SolidColor(Color.Black) else Brush.radialGradient(listOf(color.copy(alpha = 0.8f), color)),
                    shape = CircleShape
                )
                .border(if (isShadow) 0.dp else 2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
        )
        return
    }

    val emoji = when(cleanName) {
        "CAT", "KEDI" -> "🐱"
        "DOG", "KÖPEK" -> "🐶"
        "BIRD", "KUŞ" -> "🐦"
        "FISH", "BALIK" -> "🐟"
        "MONKEY", "MAYMUN" -> "🐒"
        "BANANA", "MUZ" -> "🍌"
        "BEE", "ARI" -> "🐝"
        "FLOWER", "ÇIÇEK" -> "🌸"
        "APPLE", "ELMA" -> "🍎"
        "TREE", "AĞAÇ" -> "🌳"
        "BONE", "KEMIK" -> "🦴"
        "NEST", "YUVA" -> "🪺"
        "CAR", "ARABA" -> "🚗"
        "TIRE", "TEKERLEK" -> "🛞"
        "ELEPHANT", "FIL" -> "🐘"
        "LION", "ASLAN" -> "🦁"
        "GIRAFFE", "ZÜRAFA" -> "🦒"
        "PENGUIN", "PENGUEN" -> "🐧"
        "STAR", "YILDIZ" -> "⭐"
        "BALL", "TOP" -> "⚽"
        "SUN", "GÜNEŞ" -> "☀️"
        "SUNGLASSES", "GÖZLÜK" -> "🕶️"
        "RAIN", "YAĞMUR" -> "🌧️"
        "UMBRELLA", "ŞEMSIYE" -> "☂️"
        "HAMMER", "ÇEKIÇ" -> "🔨"
        "NAIL", "ÇIVI" -> "📍"
        "CARROT", "HAVUÇ" -> "🥕"
        "RABBIT", "TAVŞAN" -> "🐰"
        "BUTTERFLY", "KELEBEK" -> "🦋"
        "DUCK", "ÖRDEK" -> "🦆"
        "ANT", "KARINCA" -> "🐜"
        "ORANGE", "PORTAKAL" -> "🍊"
        "SEA", "DENIZ" -> "🌊"
        "HOUSE", "EV" -> "🏠"
        "BUS", "OTOBÜS" -> "🚌"
        "BIKE", "BISIKLET" -> "🚲"
        "COW", "INEK" -> "🐮"
        "MILK", "SÜT" -> "🥛"
        "CHICKEN", "TAVUK" -> "🐔"
        "EGG", "YUMURTA" -> "🥚"
        "PENCIL", "KALEM" -> "✏️"
        "PAPER", "KAĞIT" -> "📄"
        "BRUSH", "FIRÇA" -> "🖌️"
        "PAINT", "BOYA" -> "🎨"
        "SNAKE", "YILAN" -> "🐍"
        "FROG", "KURBAĞA" -> "🐸"
        "1" -> "1"
        "2" -> "2"
        "3" -> "3"
        "4" -> "4"
        "5" -> "5"
        "6" -> "6"
        "7" -> "7"
        "8" -> "8"
        "9" -> "9"
        "10" -> "10"
        "CONE", "KÜLAH" -> "🍦"
        "CUBE", "KÜP" -> "🎲"
        "CYLINDER", "SİLİNDİR" -> "🔋"
        "PYRAMID", "PİRAMİT" -> "⛺"
        "PHONE", "TELEFON" -> "📞"
        "BELL", "ZİL" -> "🔔"
        "HORSE", "AT" -> "🐴"
        "CHILD", "ÇOCUK" -> "👶"
        "EARTH", "DÜNYA" -> "🌍"
        "CLOCK", "SAAT" -> "⏰"
        "RUG", "HALI" -> "🧶"
        "DOOR", "KAPI" -> "🚪"
        "MIRROR", "AYNA" -> "🪞"
        "CAKE", "PASTA" -> "🍰"
        "TRAIN", "TREN" -> "🚂"
        "SWAN", "KUĞU" -> "🦢"
        "GOOSE", "KAZ" -> "🪿"
        "WOLF", "KURT" -> "🐺"
        "FOX", "TİLKİ" -> "🦊"
        "RIVER", "NEHİR" -> "🏞️"
        "TAP", "MUSLUK" -> "🚰"
        "PLANE", "UÇAK" -> "✈️"
        "TIGER", "KAPLAN" -> "🐯"
        "BEAR", "AYI" -> "🐻"
        "DONKEY", "EŞEK" -> "🫏"
        "ZEBRA" -> "🦓"
        "ALARM" -> "🚨"
        "MAN", "ADAM" -> "👨"
        "WOMAN", "KADIN" -> "👩"
        "SHEEP", "KOYUN" -> "🐑"
        "TURKEY", "HİNDİ" -> "🦃"
        "LIZARD", "KERTENKELE" -> "🦎"
        "WORM", "SOLUCAN" -> "🪱"
        "EEL", "YILAN BALIĞI" -> "🐍"
        "FLY", "SİNEK" -> "🪰"
        "TRUCK", "KAMYON" -> "🚚"
        "SAW", "TESTERE" -> "🪚"
        "COAT", "CEKET" -> "🧥"
        "LEMON", "LİMON" -> "🍋"
        "GORILLA", "GORİL" -> "🦍"
        "PEN", "TÜKENMEZ KALEM" -> "🖊️"
        "ERASER", "SİLGİ" -> "🧼"
        "SHARK", "KÖPEK BALIĞI" -> "🦈"
        "WHALE", "BALİNA" -> "🐋"
        else -> null
    }

    if (emoji != null) {
        val shadowMatrix = ColorMatrix(floatArrayOf(
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        
        Text(
            text = emoji,
            fontSize = (size.value * 0.8).sp,
            modifier = if (isShadow) {
                Modifier.drawWithContent {
                    drawIntoCanvas { canvas ->
                        canvas.saveLayer(Rect(Offset.Zero, this.size), Paint().apply {
                            colorFilter = ColorFilter.colorMatrix(shadowMatrix)
                        })
                        drawContent()
                        canvas.restore()
                    }
                }
            } else Modifier
        )
    } else {
        Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
