package com.example.colorbyte.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colorbyte.data.model.HexCase
import com.example.colorbyte.data.model.ThemeMode
import com.example.colorbyte.domain.ColorByteConverter
import com.example.colorbyte.ui.viewmodel.ColorByteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ColorByteViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()

    var defaultHexInput by remember(settings.defaultColorHex) {
        mutableStateOf(settings.defaultColorHex)
    }
    var defaultHexError by remember { mutableStateOf<String?>(null) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Column {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Customize conversion and display preferences.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // APPEARANCE SECTION
        SettingsSectionHeader(title = "APPEARANCE")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Theme Mode
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ThemeMode.values().forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = settings.themeMode == mode,
                            onClick = { viewModel.updateSettings { it.copy(themeMode = mode) } },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.values().size)
                        ) {
                            Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Color
                SettingsSwitchRow(
                    title = "Dynamic Color",
                    subtitle = "Use Material You wallpaper colors (Android 12+)",
                    checked = settings.dynamicColor,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(dynamicColor = checked) }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FORMATTING SECTION
        SettingsSectionHeader(title = "CONVERSION & FORMATTING")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // HEX Letter Case
                Text(
                    text = "HEX Letter Case",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    HexCase.values().forEachIndexed { index, hexCase ->
                        SegmentedButton(
                            selected = settings.hexCase == hexCase,
                            onClick = { viewModel.updateSettings { it.copy(hexCase = hexCase) } },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = HexCase.values().size)
                        ) {
                            Text(if (hexCase == HexCase.UPPERCASE) "Uppercase (#FF0080)" else "Lowercase (#ff0080)")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Show # Prefix",
                    subtitle = "Prefix standard HEX output with # symbol",
                    checked = settings.showHexPrefix,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(showHexPrefix = checked) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Show CSS Format",
                    subtitle = "Display rgb(r, g, b) alongside standard values",
                    checked = settings.showCssFormat,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(showCssFormat = checked) }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // DISPLAY SECTIONS
        SettingsSectionHeader(title = "DISPLAY SECTIONS")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsSwitchRow(
                    title = "Show RGB Breakdown",
                    subtitle = "Display individual channel percentage cards",
                    checked = settings.showRgbBreakdown,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(showRgbBreakdown = checked) }
                    }
                )

                if (settings.showRgbBreakdown) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsSwitchRow(
                        title = "Show Channel Sliders",
                        subtitle = "Display visual 0–255 slider markers in channel cards",
                        checked = settings.showChannelSliders,
                        onCheckedChange = { checked ->
                            viewModel.updateSettings { it.copy(showChannelSliders = checked) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Show HEX Breakdown",
                    subtitle = "Display separated bytes and decimal equivalents",
                    checked = settings.showHexBreakdown,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(showHexBreakdown = checked) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Show Recent Colors",
                    subtitle = "Display last 5 colors row on Converter screen",
                    checked = settings.showRecentColors,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(showRecentColors = checked) }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BEHAVIOR SECTION
        SettingsSectionHeader(title = "BEHAVIOR & FEEDBACK")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Subtle vibration for input completion and copy actions",
                    checked = settings.hapticFeedback,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(hapticFeedback = checked) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsSwitchRow(
                    title = "Confirm Delete",
                    subtitle = "Ask for confirmation before removing saved colors",
                    checked = settings.confirmDelete,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(confirmDelete = checked) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // History Limit (50 or 100)
                Text(
                    text = "History Limit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                val limits = listOf(50, 100)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    limits.forEachIndexed { index, limit ->
                        SegmentedButton(
                            selected = settings.historyLimit == limit,
                            onClick = { viewModel.updateSettings { it.copy(historyLimit = limit) } },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = limits.size)
                        ) {
                            Text("$limit items")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // DEFAULT DEMO COLOR SECTION
        SettingsSectionHeader(title = "DEFAULT COLOR")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Default HEX Demonstration Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Color loaded when resetting the Converter",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val previewRgb = ColorByteConverter.hexToRgb(defaultHexInput)
                    val swatchColor = previewRgb?.let { Color(it.red, it.green, it.blue) } ?: Color.Transparent

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(swatchColor)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = defaultHexInput,
                        onValueChange = {
                            defaultHexInput = it
                            defaultHexError = null
                        },
                        singleLine = true,
                        placeholder = { Text("#FF0080") },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val success = viewModel.setDefaultColorHexSetting(defaultHexInput)
                            if (!success) {
                                defaultHexError = "Enter a valid 6-digit HEX color."
                            } else {
                                defaultHexError = null
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Default color updated")
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save")
                    }
                }

                if (defaultHexError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = defaultHexError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DANGER ZONE / ACTIONS
        SettingsSectionHeader(title = "DATA & RESET")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Restore all preferences to default values",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { showResetSettingsDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Clear History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Delete all saved and favorite colors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Clear")
                    }
                }
            }
        }
    }

    // Reset settings confirmation dialog
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset all settings?", fontWeight = FontWeight.Bold) },
            text = { Text("All preferences will revert to their default states. Your saved color history will be preserved.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetSettingsDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Clear history confirmation dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Delete all saved colors?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all saved history and favorite colors.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearHistoryDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearHistoryDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
