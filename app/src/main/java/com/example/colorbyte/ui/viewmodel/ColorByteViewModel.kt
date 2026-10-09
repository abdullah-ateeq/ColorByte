package com.example.colorbyte.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.colorbyte.data.model.AppSettings
import com.example.colorbyte.data.model.HexCase
import com.example.colorbyte.data.model.RgbColor
import com.example.colorbyte.data.model.SavedColor
import com.example.colorbyte.data.repository.ColorByteRepository
import com.example.colorbyte.domain.ColorByteConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ConversionMode {
    RGB_TO_HEX,
    HEX_TO_RGB
}

data class ConverterUiState(
    val conversionMode: ConversionMode = ConversionMode.RGB_TO_HEX,
    val redText: String = "255",
    val greenText: String = "0",
    val blueText: String = "128",
    val rgbErrorMessage: String? = null,
    val hexText: String = "#FF0080",
    val hexErrorMessage: String? = null,
    val currentColor: RgbColor = RgbColor(255, 0, 128),
    val currentHex: String = "#FF0080",
    val isSaveDialogOpen: Boolean = false,
    val saveColorName: String = "",
    val saveColorNotes: String = "",
    val isSaveColorFavorite: Boolean = false,
    val userMessage: String? = null
)

class ColorByteViewModel(
    private val repository: ColorByteRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val savedColors: StateFlow<List<SavedColor>> = repository.savedColorsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _converterUiState = MutableStateFlow(ConverterUiState())
    val converterUiState: StateFlow<ConverterUiState> = _converterUiState.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    val filteredSavedColors: StateFlow<List<SavedColor>> = combine(
        savedColors,
        _historySearchQuery
    ) { list, query ->
        val trimmed = query.trim()
        val filtered = if (trimmed.isEmpty()) {
            list
        } else {
            val qLower = trimmed.lowercase()
            list.filter { item ->
                item.name.lowercase().contains(qLower) ||
                    item.notes.lowercase().contains(qLower) ||
                    item.hex.lowercase().contains(qLower) ||
                    item.hex.replace("#", "").lowercase().contains(qLower) ||
                    "${item.red}, ${item.green}, ${item.blue}".contains(qLower) ||
                    "${item.red}".contains(qLower) ||
                    "${item.green}".contains(qLower) ||
                    "${item.blue}".contains(qLower)
            }
        }
        // Favorites appear first, then sorted by createdAt descending
        filtered.sortedWith(
            compareByDescending<SavedColor> { it.isFavorite }
                .thenByDescending { it.createdAt }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentColors: StateFlow<List<SavedColor>> = savedColors.combine(settings) { list, _ ->
        list.take(5)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Observe settings changes to update hex formatting if needed
        viewModelScope.launch {
            settings.collect { currentSettings ->
                val state = _converterUiState.value
                val formattedHex = ColorByteConverter.rgbToHex(
                    state.currentColor.red,
                    state.currentColor.green,
                    state.currentColor.blue,
                    currentSettings.hexCase,
                    currentSettings.showHexPrefix
                )
                _converterUiState.update { it.copy(currentHex = formattedHex) }
            }
        }
    }

    fun setConversionMode(mode: ConversionMode) {
        _converterUiState.update { it.copy(conversionMode = mode) }
    }

    fun onRedChanged(value: String) {
        // Numeric only, max 3 digits
        val filtered = value.filter { it.isDigit() }.take(3)
        _converterUiState.update { it.copy(redText = filtered) }
        recalculateFromRgb()
    }

    fun onGreenChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(3)
        _converterUiState.update { it.copy(greenText = filtered) }
        recalculateFromRgb()
    }

    fun onBlueChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(3)
        _converterUiState.update { it.copy(blueText = filtered) }
        recalculateFromRgb()
    }

    private fun recalculateFromRgb() {
        val state = _converterUiState.value
        val rText = state.redText
        val gText = state.greenText
        val bText = state.blueText

        if (rText.isBlank() || gText.isBlank() || bText.isBlank()) {
            _converterUiState.update {
                it.copy(rgbErrorMessage = "RGB values must be between 0 and 255.")
            }
            return
        }

        val r = rText.toIntOrNull()
        val g = gText.toIntOrNull()
        val b = bText.toIntOrNull()

        if (r == null || g == null || b == null || !ColorByteConverter.isValidRgb(r, g, b)) {
            _converterUiState.update {
                it.copy(rgbErrorMessage = "RGB values must be between 0 and 255.")
            }
            return
        }

        // Valid RGB!
        val newRgb = RgbColor(r, g, b)
        val currentSettings = settings.value
        val newHex = ColorByteConverter.rgbToHex(
            r, g, b,
            currentSettings.hexCase,
            currentSettings.showHexPrefix
        )

        _converterUiState.update {
            it.copy(
                currentColor = newRgb,
                currentHex = newHex,
                hexText = newHex,
                rgbErrorMessage = null,
                hexErrorMessage = null
            )
        }
    }

    fun onHexChanged(value: String) {
        // Filter valid hex input characters: # and 0-9, A-F, a-f
        val filtered = value.filter { it == '#' || it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
        _converterUiState.update { it.copy(hexText = filtered) }
        recalculateFromHex(filtered)
    }

    private fun recalculateFromHex(input: String) {
        val validationError = ColorByteConverter.getHexValidationError(input)
        if (validationError != null) {
            _converterUiState.update {
                it.copy(hexErrorMessage = validationError)
            }
            return
        }

        val rgb = ColorByteConverter.hexToRgb(input)
        if (rgb != null) {
            val currentSettings = settings.value
            val formattedHex = ColorByteConverter.rgbToHex(
                rgb.red, rgb.green, rgb.blue,
                currentSettings.hexCase,
                currentSettings.showHexPrefix
            )

            _converterUiState.update {
                it.copy(
                    currentColor = rgb,
                    currentHex = formattedHex,
                    redText = rgb.red.toString(),
                    greenText = rgb.green.toString(),
                    blueText = rgb.blue.toString(),
                    hexErrorMessage = null,
                    rgbErrorMessage = null
                )
            }
        }
    }

    fun resetToDefault() {
        val currentSettings = settings.value
        val defaultHex = currentSettings.defaultColorHex
        val defaultRgb = ColorByteConverter.hexToRgb(defaultHex) ?: RgbColor(255, 0, 128)
        loadColor(defaultRgb)
    }

    fun loadColor(rgb: RgbColor, name: String = "", notes: String = "") {
        val currentSettings = settings.value
        val formattedHex = ColorByteConverter.rgbToHex(
            rgb.red, rgb.green, rgb.blue,
            currentSettings.hexCase,
            currentSettings.showHexPrefix
        )

        _converterUiState.update {
            it.copy(
                currentColor = rgb,
                currentHex = formattedHex,
                redText = rgb.red.toString(),
                greenText = rgb.green.toString(),
                blueText = rgb.blue.toString(),
                hexText = formattedHex,
                rgbErrorMessage = null,
                hexErrorMessage = null,
                saveColorName = name,
                saveColorNotes = notes
            )
        }
    }

    fun openSaveDialog() {
        val state = _converterUiState.value
        if (state.rgbErrorMessage != null || state.hexErrorMessage != null) {
            setUserMessage("Enter a valid color before saving.")
            return
        }
        _converterUiState.update {
            it.copy(
                isSaveDialogOpen = true,
                saveColorName = it.saveColorName,
                saveColorNotes = it.saveColorNotes,
                isSaveColorFavorite = false
            )
        }
    }

    fun closeSaveDialog() {
        _converterUiState.update { it.copy(isSaveDialogOpen = false) }
    }

    fun onSaveNameChanged(name: String) {
        _converterUiState.update { it.copy(saveColorName = name.take(60)) }
    }

    fun onSaveNotesChanged(notes: String) {
        _converterUiState.update { it.copy(saveColorNotes = notes.take(300)) }
    }

    fun onSaveFavoriteChanged(isFavorite: Boolean) {
        _converterUiState.update { it.copy(isSaveColorFavorite = isFavorite) }
    }

    fun saveCurrentColor(onSaved: (() -> Unit)? = null) {
        val state = _converterUiState.value
        val hexValue = ColorByteConverter.rgbToHex(
            state.currentColor.red,
            state.currentColor.green,
            state.currentColor.blue
        )

        val colorToSave = SavedColor(
            name = state.saveColorName.trim(),
            notes = state.saveColorNotes.trim(),
            red = state.currentColor.red,
            green = state.currentColor.green,
            blue = state.currentColor.blue,
            hex = hexValue,
            isFavorite = state.isSaveColorFavorite
        )

        viewModelScope.launch {
            val result = repository.saveColor(colorToSave)
            result.onSuccess {
                _converterUiState.update {
                    it.copy(
                        isSaveDialogOpen = false,
                        userMessage = "Color saved"
                    )
                }
                onSaved?.invoke()
            }.onFailure { error ->
                setUserMessage(error.message ?: "Failed to save color")
            }
        }
    }

    fun toggleFavorite(colorId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(colorId)
        }
    }

    fun deleteColor(colorId: String) {
        viewModelScope.launch {
            repository.deleteColor(colorId)
            setUserMessage("Color deleted")
        }
    }

    fun duplicateColor(colorId: String) {
        viewModelScope.launch {
            val result = repository.duplicateColor(colorId)
            result.onSuccess {
                setUserMessage("Duplicated: ${it.displayName}")
            }.onFailure { error ->
                setUserMessage(error.message ?: "Failed to duplicate")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            setUserMessage("History cleared")
        }
    }

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun updateSettings(update: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(update)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            repository.resetSettings()
            setUserMessage("Settings reset to defaults")
        }
    }

    fun setDefaultColorHexSetting(hex: String): Boolean {
        val validation = ColorByteConverter.getHexValidationError(hex)
        if (validation != null) {
            setUserMessage(validation)
            return false
        }
        val normalized = ColorByteConverter.normalizeHex(hex) ?: return false
        val formatted = "#$normalized"
        updateSettings { it.copy(defaultColorHex = formatted) }
        setUserMessage("Default color updated to $formatted")
        return true
    }

    fun setUserMessage(message: String) {
        _converterUiState.update { it.copy(userMessage = message) }
    }

    fun clearUserMessage() {
        _converterUiState.update { it.copy(userMessage = null) }
    }
}
