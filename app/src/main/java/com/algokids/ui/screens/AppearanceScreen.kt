package com.algokids.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algokids.BuildConfig
import com.algokids.ads.RewardAvailability
import com.algokids.ads.WorkshopAds

@Composable
fun AppearanceScreen(language: AppLanguage, ads: WorkshopAds, stopAudio: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember(context) { context.getSharedPreferences("algokids_progress", 0) }
    var unlocked by remember { mutableStateOf(preferences.getBoolean("cosmetic_sunset_unlocked", false)) }
    var selected by remember { mutableStateOf(preferences.getString("cosmetic_theme", "ocean") ?: "ocean") }
    var confirm by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var rewardTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(ads) {
        while (true) {
            kotlinx.coroutines.delay(1_000)
            rewardTick++
        }
    }
    val rewardAvailability = remember(rewardTick) { ads.rewardAvailability }
    val rewardReady = rewardAvailability == RewardAvailability.READY
    LaunchedEffect(rewardAvailability) {
        android.util.Log.i("AlgoKidsAds", "Appearance rewarded availability=$rewardAvailability")
    }
    fun select(theme: String) {
        selected = theme
        preferences.edit().putString("cosmetic_theme", theme).apply()
    }

    BackHandler(onBack = onBack)
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 4.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(label(language, "Geri", "Back"))
        }

        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Row(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(shape = CircleShape, color = Color.White.copy(alpha = .8f), modifier = Modifier.size(58.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp)) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label(language, "Renklerini seç", "Choose your colours"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text(label(language, "AlgoKids’i sana göre renklendir.", "Give AlgoKids a colour of its own."), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Text(label(language, "GÖRÜNÜM TEMALARI", "LOOK AND FEEL"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)

        ThemeOptionCard(
            title = label(language, "Okyanus", "Ocean"),
            subtitle = label(language, "Canlı turkuaz ve ferah keşif alanları", "Bright teal and fresh discovery spaces"),
            badge = label(language, "ÜCRETSİZ", "FREE"),
            preview = listOf(Color(0xFF1C9292), Color(0xFFBDEBE1), Color(0xFFFFD69B)),
            selected = selected == "ocean",
            enabled = true,
            onClick = { select("ocean") },
            testTag = "theme_ocean",
            chooseLabel = label(language, "Seç", "Choose")
        )

        ThemeOptionCard(
            title = label(language, "Gün batımı", "Sunset"),
            subtitle = label(language, "Sıcak toprak ve yumuşak yeşil tonları", "Warm earth and gentle green tones"),
            badge = if (unlocked) label(language, "AÇILDI", "UNLOCKED") else label(language, "ÖDÜL", "REWARD"),
            preview = listOf(Color(0xFF9E5D42), Color(0xFFE9C9A9), Color(0xFFB8C99B)),
            selected = selected == "sunset",
            enabled = unlocked,
            onClick = { select("sunset") },
            testTag = "theme_sunset",
            chooseLabel = label(language, "Seç", "Choose")
        )

        if (!unlocked && BuildConfig.REWARDED_ENABLED) {
            Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.secondary)
                        Text(label(language, "İsteğe bağlı sürpriz", "An optional surprise"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(label(language, "İstersen bir ödüllü reklamı tamamlayarak Gün batımı temasını açabilirsin. Tüm oyunlar ve öğrenme içerikleri ücretsiz kalır.", "You can unlock Sunset by choosing to complete a rewarded ad. All games and learning content remain free."), style = MaterialTheme.typography.bodySmall)
                    when (rewardAvailability) {
                        RewardAvailability.READY -> Button(
                            onClick = { confirm = true },
                            modifier = Modifier.fillMaxWidth().testTag("reward_theme"),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text(label(language, "Gün batımı temasını aç", "Unlock Sunset theme")) }
                        RewardAvailability.LOADING -> Row(
                            Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text(label(language, "İlk reklam hazırlanıyor; birkaç saniye sürebilir.", "Getting the first ad ready; this may take a few seconds."), style = MaterialTheme.typography.bodySmall)
                        }
                        RewardAvailability.RETRY -> {
                            Text(label(language, "Şu an reklam bulunamadı. İstersen yeniden deneyebilirsin.", "No ad is available right now. You can try again."), style = MaterialTheme.typography.bodySmall)
                            OutlinedButton(
                                onClick = ads::retryReward,
                                modifier = Modifier.fillMaxWidth().testTag("retry_rewarded_ad"),
                                shape = RoundedCornerShape(16.dp)
                            ) { Text(label(language, "Yeniden dene", "Try again")) }
                        }
                        RewardAvailability.COOLDOWN -> Text(label(language, "Kısa bir moladan sonra ödül reklamı yeniden açılabilir.", "The reward ad will be available again after a short break."), style = MaterialTheme.typography.bodySmall)
                        RewardAvailability.SESSION_LIMIT -> Text(label(language, "Bugünlük ödül reklamı sınırına ulaşıldı. Sonra tekrar deneyebilirsin.", "The reward ad limit for this session has been reached. You can try again later."), style = MaterialTheme.typography.bodySmall)
                        RewardAvailability.DISABLED -> Text(label(language, "Bu anda ödüllü reklam açılamıyor.", "A rewarded ad cannot be opened right now."), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }

    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        title = { Text(label(language, "Gün batımı temasını aç?", "Unlock the Sunset theme?")) },
        text = { Text(label(language, "Bu tamamen isteğe bağlı. Reklamı tamamladığında yalnızca renk teması açılır; oyun ilerlemen etkilenmez.", "This is optional. Completing the ad unlocks only the colour theme; your game progress is unaffected.")) },
        confirmButton = {
            TextButton(onClick = {
                confirm = false
                ads.rewardTheme(stopAudio) { earned ->
                    unlocked = preferences.getBoolean("cosmetic_sunset_unlocked", false)
                    message = if (earned) label(language, "Gün batımı açıldı! Kartına dokunup seçebilirsin.", "Sunset is unlocked! Tap its card to select it.")
                    else label(language, "Reklam şu an tamamlanamadı. Oyununa devam edebilirsin.", "The ad could not be completed right now. You can keep playing.")
                }
            }, modifier = Modifier.testTag("confirm_reward")) { Text(label(language, "Reklamı izle", "Watch ad")) }
        },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text(label(language, "Şimdi değil", "Not now")) } }
    )
}

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    badge: String,
    preview: List<Color>,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
    chooseLabel: String
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.fillMaxWidth().height(94.dp)
                    .background(Brush.horizontalGradient(preview), RoundedCornerShape(17.dp))
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    repeat(3) { index ->
                        Surface(
                            modifier = Modifier.size(width = 48.dp + 4.dp * index.toFloat(), height = 14.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = .84f - index * .1f)
                        ) {}
                    }
                }
                Surface(Modifier.align(Alignment.BottomEnd), shape = RoundedCornerShape(50), color = Color.White.copy(alpha = .9f)) {
                    Text(badge, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF334B55))
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (selected) Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                } else if (enabled) Text(chooseLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                else Text("🔒", fontSize = 20.sp)
            }
        }
    }
}
