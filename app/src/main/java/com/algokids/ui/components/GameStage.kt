package com.algokids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameStage(
    title: String,
    progressText: String,          // örn: "2/10"
    stars: Int,                    // 0..3
    onBack: () -> Unit,
    onReplayVoice: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(Modifier.fillMaxSize()) {

        // ✅ bg_soft_algo yerine: soft gradient background
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color(0xFFFFF8E1))
                    )
                )
        )

        // ✅ TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack, shape = RoundedCornerShape(16.dp)) { Text("⬅") }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(progressText, fontWeight = FontWeight.Bold)
                Text("⭐".repeat(stars.coerceIn(0, 3)), fontSize = 18.sp)
            }

            Button(onClick = onReplayVoice, shape = RoundedCornerShape(16.dp)) { Text("🔊") }
        }

        // ✅ GAME CARD (ortada)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .background(Color(0xFFFFF8EE), RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF5D4037)
            )

            Spacer(Modifier.height(16.dp))
            content()
            Spacer(Modifier.height(16.dp))

            Button(onClick = onReplayVoice, shape = RoundedCornerShape(24.dp)) {
                Text("🔁 Tekrar Dinle")
            }
        }
    }
}
