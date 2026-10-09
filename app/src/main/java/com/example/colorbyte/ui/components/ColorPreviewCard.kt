package com.example.colorbyte.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colorbyte.data.model.AppSettings
import com.example.colorbyte.data.model.RgbColor
import com.example.colorbyte.domain.ColorByteConverter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPreviewCard(
    rgbColor: RgbColor,
    hexValue: String,
    settings: AppSettings,
    onCopyHex: () -> Unit,
    onCopyRgb: () -> Unit,
    onCopyCss: () -> Unit,
    onCopyAll: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = Color(rgbColor.red, rgbColor.green, rgbColor.blue),
        animationSpec = tween(durationMillis = 200),
        label = "ColorPreviewAnimation"
    )

    val textColorInt = ColorByteConverter.getContrastingTextColor(
        rgbColor.red,
        rgbColor.green,
        rgbColor.blue
    )
    val previewTextColor = Color(textColorInt)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Color preview card for HEX $hexValue and RGB ${rgbColor.red}, ${rgbColor.green}, ${rgbColor.blue}"
            },
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
                    text = "COLOR PREVIEW",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "HEX ↔ RGB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Hero Color Swatch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(animatedColor)
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = hexValue,
                        color = previewTextColor,
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RGB ${rgbColor.red}, ${rgbColor.green}, ${rgbColor.blue}",
                        color = previewTextColor.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace
                    )
                    if (settings.showCssFormat) {
                        Text(
                            text = ColorByteConverter.formatCssRgb(rgbColor),
                            color = previewTextColor.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Copy and Core Actions in FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onCopyHex,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy HEX",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("COPY HEX")
                }

                FilledTonalButton(
                    onClick = onCopyRgb,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy RGB",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("COPY RGB")
                }

                if (settings.showCssFormat) {
                    OutlinedButton(
                        onClick = onCopyCss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("COPY CSS")
                    }
                }

                OutlinedButton(
                    onClick = onCopyAll,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("COPY ALL")
                }

                FilledTonalButton(
                    onClick = onSave,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save color",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAVE")
                }

                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share color",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SHARE")
                }

                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset color",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESET")
                }
            }
        }
    }
}
