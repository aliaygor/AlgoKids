package com.algokids.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AlgoColors = lightColorScheme(
    primary=Color(0xFF167C80), onPrimary=Color.White,
    secondary=Color(0xFFE77952), onSecondary=Color.White,
    tertiary=Color(0xFF7462CE), background=Color(0xFFFFF8ED),
    surface=Color.White, onSurface=Color(0xFF20334A), onBackground=Color(0xFF20334A),
    surfaceVariant=Color(0xFFEAF5F1), onSurfaceVariant=Color(0xFF40566B),
    primaryContainer=Color(0xFFD7F2EA), onPrimaryContainer=Color(0xFF0B5557),
    secondaryContainer=Color(0xFFFFE4D6), onSecondaryContainer=Color(0xFF78351F),
    tertiaryContainer=Color(0xFFEAE5FF), onTertiaryContainer=Color(0xFF45368F)
)

@Composable
fun AlgoKidsTheme(content: @Composable () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = androidx.compose.runtime.remember { context.getSharedPreferences("algokids_progress",0) }
    val sunset = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(prefs.getString("cosmetic_theme","ocean")=="sunset") }
    androidx.compose.runtime.DisposableEffect(prefs) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if(key=="cosmetic_theme") sunset.value=prefs.getString(key,"ocean")=="sunset"
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    val colors = if(sunset.value) AlgoColors.copy(primary=Color(0xFF80503A),secondary=Color(0xFF65764B),
        background=Color(0xFFF6EDE3),surface=Color(0xFFFFFAF4),surfaceVariant=Color(0xFFEADACB)) else AlgoColors
    MaterialTheme(colorScheme=colors,typography=Typography,content=content)
}
