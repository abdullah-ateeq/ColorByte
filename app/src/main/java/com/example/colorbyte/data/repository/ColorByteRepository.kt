package com.example.colorbyte.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.colorbyte.data.model.AppSettings
import com.example.colorbyte.data.model.HexCase
import com.example.colorbyte.data.model.SavedColor
import com.example.colorbyte.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.io.IOException

val Context.colorByteDataStore: DataStore<Preferences> by preferencesDataStore(name = "colorbyte_preferences")

class ColorByteRepository(private val context: Context) {

    private object PreferencesKeys {
        val SAVED_COLORS = stringPreferencesKey("saved_colors")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val HEX_CASE = stringPreferencesKey("hex_case")
        val SHOW_HEX_PREFIX = booleanPreferencesKey("show_hex_prefix")
        val SHOW_CSS_FORMAT = booleanPreferencesKey("show_css_format")
        val SHOW_HEX_BREAKDOWN = booleanPreferencesKey("show_hex_breakdown")
        val SHOW_RGB_BREAKDOWN = booleanPreferencesKey("show_rgb_breakdown")
        val SHOW_CHANNEL_SLIDERS = booleanPreferencesKey("show_channel_sliders")
        val SHOW_RECENT_COLORS = booleanPreferencesKey("show_recent_colors")
        val CONFIRM_DELETE = booleanPreferencesKey("confirm_delete")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val HISTORY_LIMIT = intPreferencesKey("history_limit")
        val DEFAULT_COLOR_HEX = stringPreferencesKey("default_color_hex")
    }

    val settingsFlow: Flow<AppSettings> = context.colorByteDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            }

            val hexCaseStr = preferences[PreferencesKeys.HEX_CASE] ?: HexCase.UPPERCASE.name
            val hexCase = try {
                HexCase.valueOf(hexCaseStr)
            } catch (_: Exception) {
                HexCase.UPPERCASE
            }

            AppSettings(
                themeMode = themeMode,
                dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: false,
                hexCase = hexCase,
                showHexPrefix = preferences[PreferencesKeys.SHOW_HEX_PREFIX] ?: true,
                showCssFormat = preferences[PreferencesKeys.SHOW_CSS_FORMAT] ?: true,
                showHexBreakdown = preferences[PreferencesKeys.SHOW_HEX_BREAKDOWN] ?: true,
                showRgbBreakdown = preferences[PreferencesKeys.SHOW_RGB_BREAKDOWN] ?: true,
                showChannelSliders = preferences[PreferencesKeys.SHOW_CHANNEL_SLIDERS] ?: true,
                showRecentColors = preferences[PreferencesKeys.SHOW_RECENT_COLORS] ?: true,
                confirmDelete = preferences[PreferencesKeys.CONFIRM_DELETE] ?: true,
                hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
                historyLimit = preferences[PreferencesKeys.HISTORY_LIMIT] ?: 100,
                defaultColorHex = preferences[PreferencesKeys.DEFAULT_COLOR_HEX] ?: "#FF0080"
            )
        }

    val savedColorsFlow: Flow<List<SavedColor>> = context.colorByteDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonString = preferences[PreferencesKeys.SAVED_COLORS] ?: "[]"
            deserializeSavedColors(jsonString)
        }

    suspend fun saveColor(color: SavedColor): Result<Unit> {
        val currentSettings = settingsFlow.first()
        var errorResult: String? = null

        context.colorByteDataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.SAVED_COLORS] ?: "[]"
            val currentList = deserializeSavedColors(currentJson).toMutableList()

            if (currentList.size >= currentSettings.historyLimit) {
                // Find oldest non-favorite item
                val oldestNonFavorite = currentList
                    .filter { !it.isFavorite }
                    .minByOrNull { it.createdAt }

                if (oldestNonFavorite != null) {
                    currentList.remove(oldestNonFavorite)
                } else {
                    // All items are favorites!
                    errorResult = "History limit reached. Remove an item before saving."
                    return@edit
                }
            }

            // Prepend new item
            currentList.add(0, color)
            preferences[PreferencesKeys.SAVED_COLORS] = serializeSavedColors(currentList)
        }

        return if (errorResult != null) {
            Result.failure(IllegalStateException(errorResult))
        } else {
            Result.success(Unit)
        }
    }

    suspend fun toggleFavorite(colorId: String) {
        context.colorByteDataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.SAVED_COLORS] ?: "[]"
            val currentList = deserializeSavedColors(currentJson).map { item ->
                if (item.id == colorId) {
                    item.copy(isFavorite = !item.isFavorite)
                } else {
                    item
                }
            }
            preferences[PreferencesKeys.SAVED_COLORS] = serializeSavedColors(currentList)
        }
    }

    suspend fun deleteColor(colorId: String) {
        context.colorByteDataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.SAVED_COLORS] ?: "[]"
            val currentList = deserializeSavedColors(currentJson).filterNot { it.id == colorId }
            preferences[PreferencesKeys.SAVED_COLORS] = serializeSavedColors(currentList)
        }
    }

    suspend fun duplicateColor(colorId: String): Result<SavedColor> {
        val currentSettings = settingsFlow.first()
        var duplicatedItem: SavedColor? = null
        var errorResult: String? = null

        context.colorByteDataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.SAVED_COLORS] ?: "[]"
            val currentList = deserializeSavedColors(currentJson).toMutableList()
            val original = currentList.find { it.id == colorId } ?: return@edit

            if (currentList.size >= currentSettings.historyLimit) {
                val oldestNonFavorite = currentList
                    .filter { !it.isFavorite }
                    .minByOrNull { it.createdAt }

                if (oldestNonFavorite != null) {
                    currentList.remove(oldestNonFavorite)
                } else {
                    errorResult = "History limit reached. Remove an item before duplicating."
                    return@edit
                }
            }

            val copyName = if (original.name.isBlank()) {
                "Copy of Untitled Color"
            } else {
                "Copy of ${original.name}"
            }

            val copy = original.copy(
                id = java.util.UUID.randomUUID().toString(),
                name = copyName,
                createdAt = System.currentTimeMillis(),
                isFavorite = false
            )
            duplicatedItem = copy
            currentList.add(0, copy)
            preferences[PreferencesKeys.SAVED_COLORS] = serializeSavedColors(currentList)
        }

        return when {
            errorResult != null -> Result.failure(IllegalStateException(errorResult))
            duplicatedItem != null -> Result.success(duplicatedItem!!)
            else -> Result.failure(IllegalArgumentException("Original color not found"))
        }
    }

    suspend fun clearHistory() {
        context.colorByteDataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVED_COLORS] = "[]"
        }
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        val current = settingsFlow.first()
        val updated = update(current)

        context.colorByteDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = updated.themeMode.name
            preferences[PreferencesKeys.DYNAMIC_COLOR] = updated.dynamicColor
            preferences[PreferencesKeys.HEX_CASE] = updated.hexCase.name
            preferences[PreferencesKeys.SHOW_HEX_PREFIX] = updated.showHexPrefix
            preferences[PreferencesKeys.SHOW_CSS_FORMAT] = updated.showCssFormat
            preferences[PreferencesKeys.SHOW_HEX_BREAKDOWN] = updated.showHexBreakdown
            preferences[PreferencesKeys.SHOW_RGB_BREAKDOWN] = updated.showRgbBreakdown
            preferences[PreferencesKeys.SHOW_CHANNEL_SLIDERS] = updated.showChannelSliders
            preferences[PreferencesKeys.SHOW_RECENT_COLORS] = updated.showRecentColors
            preferences[PreferencesKeys.CONFIRM_DELETE] = updated.confirmDelete
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = updated.hapticFeedback
            preferences[PreferencesKeys.HISTORY_LIMIT] = updated.historyLimit
            preferences[PreferencesKeys.DEFAULT_COLOR_HEX] = updated.defaultColorHex
        }
    }

    suspend fun resetSettings() {
        val defaults = AppSettings()
        context.colorByteDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = defaults.themeMode.name
            preferences[PreferencesKeys.DYNAMIC_COLOR] = defaults.dynamicColor
            preferences[PreferencesKeys.HEX_CASE] = defaults.hexCase.name
            preferences[PreferencesKeys.SHOW_HEX_PREFIX] = defaults.showHexPrefix
            preferences[PreferencesKeys.SHOW_CSS_FORMAT] = defaults.showCssFormat
            preferences[PreferencesKeys.SHOW_HEX_BREAKDOWN] = defaults.showHexBreakdown
            preferences[PreferencesKeys.SHOW_RGB_BREAKDOWN] = defaults.showRgbBreakdown
            preferences[PreferencesKeys.SHOW_CHANNEL_SLIDERS] = defaults.showChannelSliders
            preferences[PreferencesKeys.SHOW_RECENT_COLORS] = defaults.showRecentColors
            preferences[PreferencesKeys.CONFIRM_DELETE] = defaults.confirmDelete
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = defaults.hapticFeedback
            preferences[PreferencesKeys.HISTORY_LIMIT] = defaults.historyLimit
            preferences[PreferencesKeys.DEFAULT_COLOR_HEX] = defaults.defaultColorHex
        }
    }

    companion object {
        fun serializeSavedColors(colors: List<SavedColor>): String {
            val jsonArray = JSONArray()
            colors.forEach { jsonArray.put(it.toJson()) }
            return jsonArray.toString()
        }

        fun deserializeSavedColors(jsonString: String): List<SavedColor> {
            if (jsonString.isBlank()) return emptyList()
            return try {
                val jsonArray = JSONArray(jsonString)
                val list = mutableListOf<SavedColor>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(SavedColor.fromJson(obj))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
