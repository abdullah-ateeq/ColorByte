package com.example.colorbyte.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colorbyte.data.model.RgbColor

@Composable
fun RgbBreakdownCard(
    rgbColor: RgbColor,
    showSliders: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RGB BREAKDOWN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "${((rgbColor.red + rgbColor.green + rgbColor.blue) / (3f * 255f) * 100).toInt()}% AVG",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChannelCard(
                    title = "RED",
                    value = rgbColor.red,
                    color = Color(0xFFEF4444),
                    showSlider = showSliders,
                    modifier = Modifier.weight(1f)
                )

                ChannelCard(
                    title = "GREEN",
                    value = rgbColor.green,
                    color = Color(0xFF10B981),
                    showSlider = showSliders,
                    modifier = Modifier.weight(1f)
                )

                ChannelCard(
                    title = "BLUE",
                    value = rgbColor.blue,
                    color = Color(0xFF3B82F6),
                    showSlider = showSliders,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ChannelCard(
    title: String,
    value: Int,
    color: Color,
    showSlider: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$value",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val percentage = (value / 255f * 100).toInt()
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            if (showSlider) {
                Spacer(modifier = Modifier.height(10.dp))
                LightweightChannelSlider(
                    fraction = value / 255f,
                    activeColor = color
                )
            }
        }
    }
}

@Composable
fun LightweightChannelSlider(
    fraction: Float,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val markerSize = 10.dp
            val clampedFraction = fraction.coerceIn(0f, 1f)
            val markerOffset = (totalWidth - markerSize) * clampedFraction

            // Track line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Active Track fill
            Box(
                modifier = Modifier
                    .width(totalWidth * clampedFraction)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(activeColor.copy(alpha = 0.7f))
            )

            // Marker
            Box(
                modifier = Modifier
                    .offset(x = markerOffset)
                    .size(markerSize)
                    .clip(CircleShape)
                    .background(activeColor)
                    .border(1.dp, Color.White, CircleShape)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = "255",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
