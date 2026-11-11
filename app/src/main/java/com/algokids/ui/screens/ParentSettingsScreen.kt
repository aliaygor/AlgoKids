package com.algokids.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.algokids.notifications.ReminderScheduler
import com.algokids.speech.ChildNarration

@Composable
fun ParentSettingsScreen(
    language: AppLanguage,
    tts: TextToSpeech,
    ready: Boolean,
    onBack: () -> Unit,
    privacyOptionsRequired: Boolean = false,
    onPrivacyOptions: () -> Unit = {},
    bannerContent: @Composable () -> Unit = {},
    onAppearance: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember(context) { ReminderScheduler.prefs(context) }
    var days by remember { mutableIntStateOf(prefs.getInt("reminder_days", 2)) }
    var pendingDays by rememberSaveable { mutableIntStateOf(2) }
    var message by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(prefs.getInt("child_age", 0)) }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            days = pendingDays
            ReminderScheduler.configure(context, days)
        } else {
            days = pendingDays
            ReminderScheduler.configure(context, pendingDays)
            message = label(language, "Bildirim izni kapalı. Android ayarlarından izin verirsen seçtiğin hatırlatıcılar başlayacak.", "Notifications are blocked. Your reminder will start if you allow notifications in Android settings.")
        }
    }

    fun setDays(value: Int) {
        if (value > 0 && Build.VERSION.SDK_INT >= 33 && !ReminderScheduler.permitted(context)) {
            pendingDays = value
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            days = value
            ReminderScheduler.configure(context, value)
        }
    }

    fun setAge(value: Int) {
        age = value.coerceIn(4, 12)
        prefs.edit().putInt("child_age", age).apply()
    }

    BackHandler(onBack = onBack)
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
            Text(label(language, "‹ Ana sayfa", "‹ Home"))
        }
        Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Row(
                Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(54.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("🏡", style = MaterialTheme.typography.headlineSmall) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label(language, "Ebeveyn köşesi", "Parent corner"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(label(language, "Aile tercihleri tek yerde.", "Family settings, all in one place."), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(label(language, "Günlük keşif seviyesi", "Daily discovery level"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(label(language, "Yaş aralığı günlük keşif sorularının seviyesini belirler. İsim veya doğum tarihi gerekmez.", "The age range sets the level of daily discovery questions. No name or birth date is needed."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { setAge((if (age == 0) 6 else age - 1).coerceIn(4, 12)) },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(44.dp).testTag("age_decrease")
                    ) { Text("−", style = MaterialTheme.typography.titleLarge) }
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.weight(1f)) {
                        Text(
                            if (age == 0) label(language, "Seçilmedi", "Not set") else label(language, "$age yaş", "Age $age"),
                            modifier = Modifier.padding(vertical = 9.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    OutlinedButton(
                        onClick = { setAge(if (age == 0) 7 else age + 1) },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(44.dp).testTag("age_increase")
                    ) { Text("+", style = MaterialTheme.typography.titleLarge) }
                    if (age > 0) TextButton(onClick = { age = 0; prefs.edit().remove("child_age").apply() }) {
                        Text(label(language, "Temizle", "Clear"))
                    }
                }
            }
        }

        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(label(language, "Hatırlatıcılar", "Reminders"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(label(language, "Varsayılan: 2 günde bir, sessiz ve en fazla bir hatırlatma. İstersen 3 güne alabilir veya kapatabilirsin.", "Default: one quiet reminder every 2 days. Choose every 3 days or turn reminders off."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(
                        0 to label(language, "Kapalı", "Off"),
                        2 to label(language, "2 günde", "Every 2 days"),
                        3 to label(language, "3 günde", "Every 3 days")
                    ).forEach { (value, title) ->
                        FilterChip(
                            selected = days == value,
                            onClick = { setDays(value) },
                            label = { Text(title) },
                            modifier = Modifier.testTag("reminder_$value")
                        )
                    }
                }
                if (!ReminderScheduler.permitted(context)) {
                    Text(label(language, "Bildirim izni kapalı.", "Notifications are disabled."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = {
                        val settings = if (Build.VERSION.SDK_INT >= 26)
                            Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                        else Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:${context.packageName}"))
                        context.startActivity(settings)
                    }) { Text(label(language, "Bildirim ayarlarını aç", "Open notification settings")) }
                }
            }
        }

        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(label(language, "Sesli anlatım", "Narration"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val available = ready && ChildNarration.configure(tts, language)
                Text(
                    if (available) label(language, "Çevrimdışı ses hazır · ${language.name}", "Offline voice ready · ${language.name}")
                    else label(language, "Bu dil için cihazda ses paketi yok. Yazılı yönergeler kullanılabilir.", "No voice is installed for this language. Written instructions are available."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                var selectedVoice by remember(language) { mutableStateOf(prefs.getString("narrator_voice_${language.name}", null)) }
                if (ready) {
                    val voices = ChildNarration.choices(tts, language)
                    voices.forEachIndexed { index, voice ->
                        val family = voice.name.removeSuffix("-local")
                        OutlinedButton(
                            onClick = {
                                selectedVoice = family
                                ChildNarration.choose(language, voice.name, prefs)
                                if (ChildNarration.configure(tts, language, story = true)) {
                                    tts.speak(label(language, "Merhaba! Birlikte düşünelim.", "Hello! Let’s think together."), TextToSpeech.QUEUE_FLUSH, null, "voice_choice")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).testTag("voice_choice_$index"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(label(language, "Anlatıcı ${index + 1}", "Narrator ${index + 1}") + if (selectedVoice == family) "  ✓" else "")
                        }
                    }
                }
                Button(
                    onClick = { speak(tts, label(language, "Merhaba! Önce düşün, sonra dene.", "Hello! Think first, then try."), language, true) },
                    enabled = available,
                    modifier = Modifier.fillMaxWidth().testTag("voice_sample")
                ) { Text(label(language, "Örnek dinle", "Play voice sample")) }
                TextButton(onClick = {
                    runCatching { context.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                }) { Text(label(language, "Ses paketi ayarları", "Voice package settings")) }
            }
        }

        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(label(language, "Gizlilik ve görünüm", "Privacy and appearance"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(label(language, "İlerleme ve tercihler bu cihazda kalır. Reklamlar çocuklara uygun ve kişiselleştirmesiz ayarlanır.", "Progress and preferences stay on this device. Ads use child-safe, non-personalized settings."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onAppearance, modifier = Modifier.weight(1f)) { Text(label(language, "Görünüm teması", "Colour theme")) }
                    if (privacyOptionsRequired) TextButton(onClick = onPrivacyOptions, modifier = Modifier.weight(1f)) { Text(label(language, "Reklam gizliliği", "Ad privacy")) }
                }
            }
        }

        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(42.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.secondary) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(label(language, "AlgoKids hakkında", "About AlgoKids"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Text(label(language, "Görüşün uygulamayı geliştirmemize yardımcı olur.", "Your feedback helps us improve the app."), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Button(
                    onClick = {
                        val market = android.net.Uri.parse("market://details?id=${context.packageName}")
                        val web = android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, market)) }
                            .recoverCatching { context.startActivity(Intent(Intent.ACTION_VIEW, web)) }
                            .onFailure { message = label(language, "Google Play açılamadı.", "Google Play could not open.") }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("google_play_review"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text(label(language, "Google Play’de değerlendir", "Rate on Google Play"), fontWeight = FontWeight.Bold) }
            }
        }
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        bannerContent()
    }
}
