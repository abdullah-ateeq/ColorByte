package com.example.colorbyte.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val rScale = remember { Animatable(0f) }
    val gScale = remember { Animatable(0f) }
    val bScale = remember { Animatable(0f) }
    val hexAlpha = remember { Animatable(0f) }
    val previewAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Three RGB channel blocks appear
        rScale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        gScale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        bScale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))

        // Step 2: Combine into HEX value and preview fades in
        hexAlpha.animateTo(1f, tween(300))
        previewAlpha.animateTo(1f, tween(300))

        // Step 3: Title appears
        titleAlpha.animateTo(1f, tween(300))

        // Hold for remaining time to reach ~1.4s total duration
        delay(250)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Three RGB channel blocks
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // R block
                Box(
                    modifier = Modifier
                        .scale(rScale.value)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "R",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }

                // G block
                Box(
                    modifier = Modifier
                        .scale(gScale.value)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }

                // B block
                Box(
                    modifier = Modifier
                        .scale(bScale.value)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "B",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Combined hero preview circle & HEX badge
            Box(
                modifier = Modifier
                    .alpha(previewAlpha.value)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF0080))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#FF0080",
                    modifier = Modifier.alpha(hexAlpha.value),
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title & Subtitle
            Column(
                modifier = Modifier.alpha(titleAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "COLORBYTE",
                    style = MaterialTheme.typography.headlineLarge,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "RGB ↔ HEX",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
