package com.example.colorbyte.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colorbyte.domain.ColorByteConverter
import com.example.colorbyte.ui.components.ColorPreviewCard
import com.example.colorbyte.ui.components.HexBreakdownCard
import com.example.colorbyte.ui.components.HexInputSection
import com.example.colorbyte.ui.components.RecentColorsRow
import com.example.colorbyte.ui.components.RgbBreakdownCard
import com.example.colorbyte.ui.components.RgbInputsSection
import com.example.colorbyte.ui.components.SaveColorDialog
import com.example.colorbyte.ui.util.HapticHelper
import com.example.colorbyte.ui.util.HapticType
import com.example.colorbyte.ui.viewmodel.ColorByteViewModel
import com.example.colorbyte.ui.viewmodel.ConversionMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: ColorByteViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.converterUiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val recentColors by viewModel.recentColors.collectAsState()

    val redFocusRequester = remember { FocusRequester() }
    val hexFocusRequester = remember { FocusRequester() }

    // User message snackbar observation
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    fun copyToClipboard(label: String, text: String, feedbackMessage: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
        coroutineScope.launch {
            snackbarHostState.showSnackbar(feedbackMessage)
        }
    }

    fun shareColor() {
        val namePart = if (uiState.saveColorName.isNotBlank()) "Name: ${uiState.saveColorName}\n" else ""
        val shareText = "ColorByte\n" +
            namePart +
            "HEX: ${uiState.currentHex}\n" +
            "RGB: ${uiState.currentColor.red}, ${uiState.currentColor.green}, ${uiState.currentColor.blue}\n" +
            "CSS: ${ColorByteConverter.formatCssRgb(uiState.currentColor)}"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Color")
        context.startActivity(shareIntent)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val isWide = maxWidth >= 600.dp
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Column {
                Text(
                    text = "COLORBYTE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Convert RGB values to HEX and back.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector: RGB → HEX vs HEX → RGB
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Conversion mode selector" }
            ) {
                SegmentedButton(
                    selected = uiState.conversionMode == ConversionMode.RGB_TO_HEX,
                    onClick = {
                        viewModel.setConversionMode(ConversionMode.RGB_TO_HEX)
                        try {
                            redFocusRequester.requestFocus()
                        } catch (_: Exception) {}
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(
                        text = "RGB → HEX",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                SegmentedButton(
                    selected = uiState.conversionMode == ConversionMode.HEX_TO_RGB,
                    onClick = {
                        viewModel.setConversionMode(ConversionMode.HEX_TO_RGB)
                        try {
                            hexFocusRequester.requestFocus()
                        } catch (_: Exception) {}
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(
                        text = "HEX → RGB",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isWide) {
                // Two-column layout for tablets and landscape
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Inputs & Breakdown
                    Column(modifier = Modifier.weight(1f)) {
                        if (uiState.conversionMode == ConversionMode.RGB_TO_HEX) {
                            RgbInputsSection(
                                redText = uiState.redText,
                                greenText = uiState.greenText,
                                blueText = uiState.blueText,
                                errorMessage = uiState.rgbErrorMessage,
                                onRedChange = viewModel::onRedChanged,
                                onGreenChange = viewModel::onGreenChanged,
                                onBlueChange = viewModel::onBlueChanged,
                                redFocusRequester = redFocusRequester
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HexInputSection(
                                hexText = uiState.hexText,
                                errorMessage = uiState.hexErrorMessage,
                                onHexChange = viewModel::onHexChanged,
                                hexFocusRequester = hexFocusRequester
                            )
                        } else {
                            HexInputSection(
                                hexText = uiState.hexText,
                                errorMessage = uiState.hexErrorMessage,
                                onHexChange = viewModel::onHexChanged,
                                hexFocusRequester = hexFocusRequester
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            RgbInputsSection(
                                redText = uiState.redText,
                                greenText = uiState.greenText,
                                blueText = uiState.blueText,
                                errorMessage = uiState.rgbErrorMessage,
                                onRedChange = viewModel::onRedChanged,
                                onGreenChange = viewModel::onGreenChanged,
                                onBlueChange = viewModel::onBlueChanged,
                                redFocusRequester = redFocusRequester
                            )
                        }

                        if (settings.showRgbBreakdown) {
                            Spacer(modifier = Modifier.height(16.dp))
                            RgbBreakdownCard(
                                rgbColor = uiState.currentColor,
                                showSliders = settings.showChannelSliders
                            )
                        }

                        if (settings.showHexBreakdown) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HexBreakdownCard(rgbColor = uiState.currentColor)
                        }
                    }

                    // Right Column: Preview, Actions, Recent Colors
                    Column(modifier = Modifier.weight(1f)) {
                        ColorPreviewCard(
                            rgbColor = uiState.currentColor,
                            hexValue = uiState.currentHex,
                            settings = settings,
                            onCopyHex = { copyToClipboard("HEX", uiState.currentHex, "HEX copied") },
                            onCopyRgb = { copyToClipboard("RGB", ColorByteConverter.formatRgb(uiState.currentColor), "RGB copied") },
                            onCopyCss = { copyToClipboard("CSS", ColorByteConverter.formatCssRgb(uiState.currentColor), "CSS copied") },
                            onCopyAll = {
                                val allValues = "HEX: ${uiState.currentHex}\nRGB: ${uiState.currentColor.red}, ${uiState.currentColor.green}, ${uiState.currentColor.blue}\nCSS: ${ColorByteConverter.formatCssRgb(uiState.currentColor)}"
                                copyToClipboard("All Values", allValues, "Color values copied")
                            },
                            onShare = ::shareColor,
                            onSave = {
                                HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                                viewModel.openSaveDialog()
                            },
                            onReset = {
                                HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                                viewModel.resetToDefault()
                            }
                        )

                        if (settings.showRecentColors && recentColors.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            RecentColorsRow(
                                recentColors = recentColors,
                                onSelectColor = { saved ->
                                    viewModel.loadColor(saved.rgb, saved.name, saved.notes)
                                }
                            )
                        }
                    }
                }
            } else {
                // Single Column Mobile Layout:
                // 1. Color Preview & Actions
                ColorPreviewCard(
                    rgbColor = uiState.currentColor,
                    hexValue = uiState.currentHex,
                    settings = settings,
                    onCopyHex = { copyToClipboard("HEX", uiState.currentHex, "HEX copied") },
                    onCopyRgb = { copyToClipboard("RGB", ColorByteConverter.formatRgb(uiState.currentColor), "RGB copied") },
                    onCopyCss = { copyToClipboard("CSS", ColorByteConverter.formatCssRgb(uiState.currentColor), "CSS copied") },
                    onCopyAll = {
                        val allValues = "HEX: ${uiState.currentHex}\nRGB: ${uiState.currentColor.red}, ${uiState.currentColor.green}, ${uiState.currentColor.blue}\nCSS: ${ColorByteConverter.formatCssRgb(uiState.currentColor)}"
                        copyToClipboard("All Values", allValues, "Color values copied")
                    },
                    onShare = ::shareColor,
                    onSave = {
                        HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                        viewModel.openSaveDialog()
                    },
                    onReset = {
                        HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                        viewModel.resetToDefault()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Inputs ordered according to mode
                if (uiState.conversionMode == ConversionMode.RGB_TO_HEX) {
                    RgbInputsSection(
                        redText = uiState.redText,
                        greenText = uiState.greenText,
                        blueText = uiState.blueText,
                        errorMessage = uiState.rgbErrorMessage,
                        onRedChange = viewModel::onRedChanged,
                        onGreenChange = viewModel::onGreenChanged,
                        onBlueChange = viewModel::onBlueChanged,
                        redFocusRequester = redFocusRequester
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HexInputSection(
                        hexText = uiState.hexText,
                        errorMessage = uiState.hexErrorMessage,
                        onHexChange = viewModel::onHexChanged,
                        hexFocusRequester = hexFocusRequester
                    )
                } else {
                    HexInputSection(
                        hexText = uiState.hexText,
                        errorMessage = uiState.hexErrorMessage,
                        onHexChange = viewModel::onHexChanged,
                        hexFocusRequester = hexFocusRequester
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RgbInputsSection(
                        redText = uiState.redText,
                        greenText = uiState.greenText,
                        blueText = uiState.blueText,
                        errorMessage = uiState.rgbErrorMessage,
                        onRedChange = viewModel::onRedChanged,
                        onGreenChange = viewModel::onGreenChanged,
                        onBlueChange = viewModel::onBlueChanged,
                        redFocusRequester = redFocusRequester
                    )
                }

                // RGB Breakdown
                if (settings.showRgbBreakdown) {
                    Spacer(modifier = Modifier.height(16.dp))
                    RgbBreakdownCard(
                        rgbColor = uiState.currentColor,
                        showSliders = settings.showChannelSliders
                    )
                }

                // HEX Breakdown
                if (settings.showHexBreakdown) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HexBreakdownCard(rgbColor = uiState.currentColor)
                }

                // Recent Colors
                if (settings.showRecentColors && recentColors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    RecentColorsRow(
                        recentColors = recentColors,
                        onSelectColor = { saved ->
                            viewModel.loadColor(saved.rgb, saved.name, saved.notes)
                        }
                    )
                }
            }
        }
    }

    // Save Color Dialog
    if (uiState.isSaveDialogOpen) {
        SaveColorDialog(
            rgbColor = uiState.currentColor,
            hexValue = uiState.currentHex,
            name = uiState.saveColorName,
            notes = uiState.saveColorNotes,
            isFavorite = uiState.isSaveColorFavorite,
            onNameChange = viewModel::onSaveNameChanged,
            onNotesChange = viewModel::onSaveNotesChanged,
            onFavoriteChange = viewModel::onSaveFavoriteChanged,
            onConfirm = {
                HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                viewModel.saveCurrentColor()
            },
            onDismiss = viewModel::closeSaveDialog
        )
    }
}
