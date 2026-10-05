package com.algokids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algokids.ui.screens.AppLanguage
import com.algokids.ui.screens.label

@Composable
fun GameScene(
    title: String, instruction: String, progress: Float = 0.5f, scoreText: String = "",
    onBack: () -> Unit, onExit: () -> Unit = {}, isSoundEnabled: Boolean = true,
    onToggleSound: () -> Unit = {}, language: AppLanguage = AppLanguage.TR,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFFF4F6FC)).safeDrawingPadding(),contentAlignment=Alignment.TopCenter) {
        val sceneHeight = maxOf(maxHeight, 640.dp)
        Column(Modifier.widthIn(max=760.dp).fillMaxWidth().verticalScroll(rememberScrollState()).height(sceneHeight).padding(16.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                IconButton(onClick=onBack) { Icon(Icons.Default.ArrowBack,label(language,"Önceki soru","Previous question")) }
                LinearProgressIndicator(progress={progress.coerceIn(0f,1f)},modifier=Modifier.weight(1f),color=Color(0xFF308263),trackColor=Color(0xFFDFE7F2))
                IconButton(onClick=onToggleSound) { Icon(if(isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,label(language,if(isSoundEnabled) "Sesi kapat" else "Sesi aç",if(isSoundEnabled) "Mute" else "Sound on")) }
                IconButton(onClick=onExit) { Icon(Icons.Default.Close,label(language,"Atölyelere dön","Back to workshops")) }
            }
            Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,color=Color(0xFF172B4D),textAlign=TextAlign.Center)
            if(scoreText.isNotBlank()) Text(scoreText,style=MaterialTheme.typography.labelLarge,color=Color(0xFF516078))
            Text(instruction,style=MaterialTheme.typography.bodyLarge,color=Color(0xFF334155),textAlign=TextAlign.Center)
            Surface(Modifier.fillMaxWidth().weight(1f),shape=RoundedCornerShape(24.dp),color=Color.White,tonalElevation=1.dp) {
                Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally) { content() }
            }
        }
    }
}
