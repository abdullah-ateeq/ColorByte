package com.example.colorbyte.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RgbInputsSection(
    redText: String,
    greenText: String,
    blueText: String,
    errorMessage: String?,
    onRedChange: (String) -> Unit,
    onGreenChange: (String) -> Unit,
    onBlueChange: (String) -> Unit,
    redFocusRequester: FocusRequester? = null,
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
                    text = "RGB CHANNELS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "0 – 255",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // RED
                ChannelInput(
                    label = "RED",
                    channelColor = Color(0xFFEF4444),
                    value = redText,
                    onValueChange = onRedChange,
                    contentDesc = "Red channel $redText",
                    focusRequester = redFocusRequester,
                    modifier = Modifier.weight(1f)
                )

                // GREEN
                ChannelInput(
                    label = "GREEN",
                    channelColor = Color(0xFF10B981),
                    value = greenText,
                    onValueChange = onGreenChange,
                    contentDesc = "Green channel $greenText",
                    focusRequester = null,
                    modifier = Modifier.weight(1f)
                )

                // BLUE
                ChannelInput(
                    label = "BLUE",
                    channelColor = Color(0xFF3B82F6),
                    value = blueText,
                    onValueChange = onBlueChange,
                    contentDesc = "Blue channel $blueText",
                    focusRequester = null,
                    modifier = Modifier.weight(1f)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.semantics {
                        contentDescription = errorMessage
                    }
                )
            }
        }
    }
}

@Composable
private fun ChannelInput(
    label: String,
    channelColor: Color,
    value: String,
    onValueChange: (String) -> Unit,
    contentDesc: String,
    focusRequester: FocusRequester?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(channelColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
                )
                .semantics {
                    contentDescription = contentDesc
                }
        )
    }
}
