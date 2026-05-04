package com.algokids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScene(
    title: String,
    instruction: String,
    progress: Float = 0.5f,
    onBack: () -> Unit,
    onExit: () -> Unit = {},
    isSoundEnabled: Boolean = true,
    onToggleSound: () -> Unit = {},
    onSound: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE1F5FE), Color(0xFFE8F5E9))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔝 Üst Panel (HUD)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Geri Butonu (Önceki Soru)
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF5D4037))
                }

                // Bitir/Çık Butonu (Ana Menü)
                IconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFFEBEE), CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFD32F2F))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))),
                                RoundedCornerShape(8.dp)
                            )
                    )
                }

                // Ses Kapatma/Açma Butonu
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (isSoundEnabled) Color.White else Color(0xFFEEEEEE), CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = null,
                        tint = if (isSoundEnabled) Color(0xFF1976D2) else Color.Gray
                    )
                }

                // Tekrar Dinle Butonu
                IconButton(
                    onClick = onSound,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Tekrar Dinle", tint = Color(0xFF1976D2))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32)
            )
            
            Text(
                text = instruction,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5D4037),
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🧩 Oyun Kartı
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f)
                    .padding(bottom = 32.dp)
                    .shadow(12.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.95f) // Hafif transparanlık
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    content()
                }
            }
        }
    }
}
