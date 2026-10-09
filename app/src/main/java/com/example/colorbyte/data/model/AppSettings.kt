package com.example.colorbyte.data.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class HexCase {
    UPPERCASE,
    LOWERCASE
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val hexCase: HexCase = HexCase.UPPERCASE,
    val showHexPrefix: Boolean = true,
    val showCssFormat: Boolean = true,
    val showHexBreakdown: Boolean = true,
    val showRgbBreakdown: Boolean = true,
    val showChannelSliders: Boolean = true,
    val showRecentColors: Boolean = true,
    val confirmDelete: Boolean = true,
    val hapticFeedback: Boolean = true,
    val historyLimit: Int = 100,
    val defaultColorHex: String = "#FF0080"
)
