package com.example.colorbyte.domain

import com.example.colorbyte.data.model.HexCase
import com.example.colorbyte.data.model.RgbColor
import kotlin.math.pow

object ColorByteConverter {

    private val HEX_REGEX = Regex("^[0-9A-Fa-f]{6}$")

    /**
     * Converts RGB values to a standard 6-digit HEX color string (#RRGGBB).
     */
    fun rgbToHex(red: Int, green: Int, blue: Int): String {
        return rgbToHex(red, green, blue, HexCase.UPPERCASE, showPrefix = true)
    }

    /**
     * Converts RGB values with custom case and prefix options.
     */
    fun rgbToHex(
        red: Int,
        green: Int,
        blue: Int,
        hexCase: HexCase,
        showPrefix: Boolean
    ): String {
        require(isValidRgb(red, green, blue)) {
            "RGB values must be between 0 and 255. Got ($red, $green, $blue)"
        }
        val format = if (hexCase == HexCase.UPPERCASE) "%02X%02X%02X" else "%02x%02x%02x"
        val hexValue = String.format(format, red, green, blue)
        return if (showPrefix) "#$hexValue" else hexValue
    }

    /**
     * Normalizes a hex string by trimming and removing any leading '#'.
     * Returns a valid 6-character uppercase hex string or null if invalid.
     */
    fun normalizeHex(hex: String): String? {
        val trimmed = hex.trim()
        val cleaned = if (trimmed.startsWith("#")) trimmed.substring(1) else trimmed
        if (!cleaned.matches(HEX_REGEX)) {
            return null
        }
        return cleaned.uppercase()
    }

    /**
     * Converts a HEX string (#RRGGBB or RRGGBB) to RgbColor, or null if invalid.
     */
    fun hexToRgb(hex: String): RgbColor? {
        val normalized = normalizeHex(hex) ?: return null
        return try {
            val r = normalized.substring(0, 2).toInt(16)
            val g = normalized.substring(2, 4).toInt(16)
            val b = normalized.substring(4, 6).toInt(16)
            RgbColor(r, g, b)
        } catch (_: NumberFormatException) {
            null
        }
    }

    /**
     * Checks if the RGB channel values are all in the 0..255 range.
     */
    fun isValidRgb(red: Int, green: Int, blue: Int): Boolean {
        return red in 0..255 && green in 0..255 && blue in 0..255
    }

    /**
     * Checks if a string is a valid 6-digit HEX color with optional leading '#'.
     */
    fun isValidHex(hex: String): Boolean {
        return normalizeHex(hex) != null
    }

    /**
     * Validates hex and returns a specific human-readable error message if invalid, or null if valid.
     */
    fun getHexValidationError(hex: String): String? {
        val trimmed = hex.trim()
        val cleaned = if (trimmed.startsWith("#")) trimmed.substring(1) else trimmed
        if (cleaned.length == 3 && cleaned.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) {
            return "Use a 6-digit HEX value such as #FFFFFF."
        }
        if (!cleaned.matches(HEX_REGEX)) {
            return "Enter a valid 6-digit HEX color."
        }
        return null
    }

    /**
     * Validates an RGB channel value.
     */
    fun getRgbChannelError(valueText: String): String? {
        if (valueText.isBlank()) {
            return "Required"
        }
        val number = valueText.toIntOrNull()
        if (number == null || number !in 0..255) {
            return "RGB values must be between 0 and 255."
        }
        return null
    }

    /**
     * Formats RGB values to standard display: RGB(255, 0, 128)
     */
    fun formatRgb(rgb: RgbColor): String {
        return "RGB(${rgb.red}, ${rgb.green}, ${rgb.blue})"
    }

    /**
     * Formats RGB values to CSS format: rgb(255, 0, 128)
     */
    fun formatCssRgb(rgb: RgbColor): String {
        return "rgb(${rgb.red}, ${rgb.green}, ${rgb.blue})"
    }

    /**
     * Calculates the standard relative luminance (WCAG 2.1) of an RGB color.
     * Returns a value between 0.0 (darkest black) and 1.0 (lightest white).
     */
    fun calculateLuminance(red: Int, green: Int, blue: Int): Double {
        fun linearize(channel: Int): Double {
            val c = channel / 255.0
            return if (c <= 0.04045) {
                c / 12.92
            } else {
                ((c + 0.055) / 1.055).pow(2.4)
            }
        }

        val rLinear = linearize(red)
        val gLinear = linearize(green)
        val bLinear = linearize(blue)

        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }

    /**
     * Determines whether dark text or light text should be used for optimal contrast.
     * Returns 0xFF121212 for dark foreground or 0xFFFFFFFF for light foreground.
     */
    fun getContrastingTextColor(red: Int, green: Int, blue: Int): Long {
        val luminance = calculateLuminance(red, green, blue)
        // Midpoint luminance for standard 4.5:1 / readability threshold
        return if (luminance > 0.179) 0xFF121212L else 0xFFFFFFFFL
    }

    /**
     * Gets the individual 2-digit hex values for R, G, B channels.
     */
    fun getHexChannelBreakdown(red: Int, green: Int, blue: Int): Triple<String, String, String> {
        val r = String.format("%02X", red)
        val g = String.format("%02X", green)
        val b = String.format("%02X", blue)
        return Triple(r, g, b)
    }
}
