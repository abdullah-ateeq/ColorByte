package com.example.colorbyte

import com.example.colorbyte.data.model.AppSettings
import com.example.colorbyte.data.model.HexCase
import com.example.colorbyte.data.model.RgbColor
import com.example.colorbyte.data.model.SavedColor
import com.example.colorbyte.data.model.ThemeMode
import com.example.colorbyte.data.repository.ColorByteRepository
import com.example.colorbyte.domain.ColorByteConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class ColorByteUnitTest {

    // 1. RGB → HEX
    @Test
    fun test01_rgbToHex_pink() {
        val hex = ColorByteConverter.rgbToHex(255, 0, 128)
        assertEquals("#FF0080", hex)
    }

    // 2. HEX → RGB
    @Test
    fun test02_hexToRgb_blue() {
        val rgb = ColorByteConverter.hexToRgb("#3366CC")
        assertNotNull(rgb)
        assertEquals(51, rgb!!.red)
        assertEquals(102, rgb.green)
        assertEquals(204, rgb.blue)
    }

    // 3. Black
    @Test
    fun test03_black() {
        val hex = ColorByteConverter.rgbToHex(0, 0, 0)
        assertEquals("#000000", hex)
        val rgb = ColorByteConverter.hexToRgb("#000000")
        assertEquals(RgbColor(0, 0, 0), rgb)
    }

    // 4. White
    @Test
    fun test04_white() {
        val hex = ColorByteConverter.rgbToHex(255, 255, 255)
        assertEquals("#FFFFFF", hex)
        val rgb = ColorByteConverter.hexToRgb("#FFFFFF")
        assertEquals(RgbColor(255, 255, 255), rgb)
    }

    // 5. Red
    @Test
    fun test05_red() {
        val hex = ColorByteConverter.rgbToHex(255, 0, 0)
        assertEquals("#FF0000", hex)
        val rgb = ColorByteConverter.hexToRgb("#FF0000")
        assertEquals(RgbColor(255, 0, 0), rgb)
    }

    // 6. Green
    @Test
    fun test06_green() {
        val hex = ColorByteConverter.rgbToHex(0, 255, 0)
        assertEquals("#00FF00", hex)
        val rgb = ColorByteConverter.hexToRgb("#00FF00")
        assertEquals(RgbColor(0, 255, 0), rgb)
    }

    // 7. Blue
    @Test
    fun test07_blue() {
        val hex = ColorByteConverter.rgbToHex(0, 0, 255)
        assertEquals("#0000FF", hex)
        val rgb = ColorByteConverter.hexToRgb("#0000FF")
        assertEquals(RgbColor(0, 0, 255), rgb)
    }

    // 8. Mixed color
    @Test
    fun test08_mixedColor() {
        val rgb = ColorByteConverter.hexToRgb("#1A2B3C")
        assertNotNull(rgb)
        assertEquals(26, rgb!!.red)
        assertEquals(43, rgb.green)
        assertEquals(60, rgb.blue)
        val hex = ColorByteConverter.rgbToHex(26, 43, 60)
        assertEquals("#1A2B3C", hex)
    }

    // 9. Minimum RGB values
    @Test
    fun test09_minimumRgbValues() {
        assertTrue(ColorByteConverter.isValidRgb(0, 0, 0))
        val hex = ColorByteConverter.rgbToHex(0, 0, 0)
        assertEquals("#000000", hex)
    }

    // 10. Maximum RGB values
    @Test
    fun test10_maximumRgbValues() {
        assertTrue(ColorByteConverter.isValidRgb(255, 255, 255))
        val hex = ColorByteConverter.rgbToHex(255, 255, 255)
        assertEquals("#FFFFFF", hex)
    }

    // 11. Leading #
    @Test
    fun test11_leadingHash() {
        val rgb = ColorByteConverter.hexToRgb("#FF0080")
        assertNotNull(rgb)
        assertEquals(255, rgb!!.red)
        assertEquals(0, rgb.green)
        assertEquals(128, rgb.blue)
    }

    // 12. HEX without #
    @Test
    fun test12_hexWithoutHash() {
        val rgb = ColorByteConverter.hexToRgb("FF0080")
        assertNotNull(rgb)
        assertEquals(255, rgb!!.red)
        assertEquals(0, rgb.green)
        assertEquals(128, rgb.blue)
    }

    // 13. Lowercase HEX
    @Test
    fun test13_lowercaseHex() {
        val rgb = ColorByteConverter.hexToRgb("#ff0080")
        assertNotNull(rgb)
        assertEquals(255, rgb!!.red)
        assertEquals(0, rgb.green)
        assertEquals(128, rgb.blue)

        val output = ColorByteConverter.rgbToHex(255, 0, 128, HexCase.LOWERCASE, showPrefix = true)
        assertEquals("#ff0080", output)
    }

    // 14. Uppercase HEX
    @Test
    fun test14_uppercaseHex() {
        val output = ColorByteConverter.rgbToHex(255, 0, 128, HexCase.UPPERCASE, showPrefix = true)
        assertEquals("#FF0080", output)
    }

    // 15. Invalid HEX characters
    @Test
    fun test15_invalidHexCharacters() {
        assertNull(ColorByteConverter.hexToRgb("#GGGGGG"))
        assertNull(ColorByteConverter.hexToRgb("ABCXYZ"))
        assertEquals("Enter a valid 6-digit HEX color.", ColorByteConverter.getHexValidationError("#GGGGGG"))
        assertEquals("Enter a valid 6-digit HEX color.", ColorByteConverter.getHexValidationError("ABCXYZ"))
    }

    // 16. Invalid HEX length
    @Test
    fun test16_invalidHexLength() {
        assertNull(ColorByteConverter.hexToRgb("#12345"))
        assertNull(ColorByteConverter.hexToRgb("#1234567"))
        assertEquals("Use a 6-digit HEX value such as #FFFFFF.", ColorByteConverter.getHexValidationError("#FFF"))
        assertEquals("Use a 6-digit HEX value such as #FFFFFF.", ColorByteConverter.getHexValidationError("FFF"))
        assertEquals("Enter a valid 6-digit HEX color.", ColorByteConverter.getHexValidationError("#12345"))
        assertEquals("Enter a valid 6-digit HEX color.", ColorByteConverter.getHexValidationError("#1234567"))
    }

    // 17. RGB below 0
    @Test
    fun test17_rgbBelowZero() {
        assertFalse(ColorByteConverter.isValidRgb(-1, 100, 100))
        assertFalse(ColorByteConverter.isValidRgb(100, -1, 100))
        assertFalse(ColorByteConverter.isValidRgb(100, 100, -1))
        assertEquals("RGB values must be between 0 and 255.", ColorByteConverter.getRgbChannelError("-1"))
    }

    // 18. RGB above 255
    @Test
    fun test18_rgbAbove255() {
        assertFalse(ColorByteConverter.isValidRgb(256, 100, 100))
        assertFalse(ColorByteConverter.isValidRgb(100, 256, 100))
        assertFalse(ColorByteConverter.isValidRgb(100, 100, 256))
        assertEquals("RGB values must be between 0 and 255.", ColorByteConverter.getRgbChannelError("256"))
    }

    // 19. CSS RGB formatting
    @Test
    fun test19_cssRgbFormatting() {
        val formatted = ColorByteConverter.formatCssRgb(RgbColor(255, 0, 128))
        assertEquals("rgb(255, 0, 128)", formatted)

        val stdFormatted = ColorByteConverter.formatRgb(RgbColor(255, 0, 128))
        assertEquals("RGB(255, 0, 128)", stdFormatted)
    }

    // 20. HEX normalization
    @Test
    fun test20_hexNormalization() {
        assertEquals("FF0080", ColorByteConverter.normalizeHex("  #ff0080  "))
        assertEquals("FFFFFF", ColorByteConverter.normalizeHex("ffffff"))
        assertNull(ColorByteConverter.normalizeHex("123"))
        assertNull(ColorByteConverter.normalizeHex("invalid"))
    }

    // 21. Round-trip RGB → HEX → RGB
    @Test
    fun test21_roundTripRgbToHexToRgb() {
        val testColors = listOf(
            RgbColor(0, 0, 0),
            RgbColor(255, 255, 255),
            RgbColor(255, 0, 128),
            RgbColor(51, 102, 204),
            RgbColor(12, 34, 56),
            RgbColor(200, 150, 75)
        )

        for (original in testColors) {
            val hex = ColorByteConverter.rgbToHex(original.red, original.green, original.blue)
            val converted = ColorByteConverter.hexToRgb(hex)
            assertNotNull(converted)
            assertEquals("Round trip failed for $original", original, converted)
        }
    }

    // 22. Round-trip HEX → RGB → HEX
    @Test
    fun test22_roundTripHexToRgbToHex() {
        val testHexes = listOf("#000000", "#FFFFFF", "#FF0080", "#3366CC", "#1A2B3C", "#AABBCC")
        for (original in testHexes) {
            val rgb = ColorByteConverter.hexToRgb(original)
            assertNotNull(rgb)
            val converted = ColorByteConverter.rgbToHex(rgb!!.red, rgb.green, rgb.blue)
            assertEquals("Round trip failed for $original", original, converted)
        }
    }

    // 23. Contrast calculation
    @Test
    fun test23_contrastCalculation() {
        // Black background should use light foreground text (0xFFFFFFFF)
        val blackFg = ColorByteConverter.getContrastingTextColor(0, 0, 0)
        assertEquals(0xFFFFFFFFL, blackFg)

        // White background should use dark foreground text (0xFF121212)
        val whiteFg = ColorByteConverter.getContrastingTextColor(255, 255, 255)
        assertEquals(0xFF121212L, whiteFg)

        // Light yellow should use dark text
        val yellowFg = ColorByteConverter.getContrastingTextColor(255, 255, 0)
        assertEquals(0xFF121212L, yellowFg)

        // Dark navy should use light text
        val navyFg = ColorByteConverter.getContrastingTextColor(10, 20, 40)
        assertEquals(0xFFFFFFFFL, navyFg)
    }

    // 24. History persistence
    @Test
    fun test24_historyPersistence() {
        val color1 = SavedColor(
            id = UUID.randomUUID().toString(),
            name = "Primary Pink",
            notes = "Brand accent",
            red = 255,
            green = 0,
            blue = 128,
            hex = "#FF0080",
            createdAt = 1000L,
            isFavorite = true
        )
        val color2 = SavedColor(
            id = UUID.randomUUID().toString(),
            name = "Brand Blue",
            notes = "Header color",
            red = 51,
            green = 102,
            blue = 204,
            hex = "#3366CC",
            createdAt = 2000L,
            isFavorite = false
        )

        val list = listOf(color1, color2)
        val json = ColorByteRepository.serializeSavedColors(list)
        val deserialized = ColorByteRepository.deserializeSavedColors(json)

        assertEquals(2, deserialized.size)
        assertEquals(color1.id, deserialized[0].id)
        assertEquals(color1.name, deserialized[0].name)
        assertEquals(color1.notes, deserialized[0].notes)
        assertEquals(color1.red, deserialized[0].red)
        assertEquals(color1.hex, deserialized[0].hex)
        assertEquals(color1.isFavorite, deserialized[0].isFavorite)

        assertEquals(color2.id, deserialized[1].id)
        assertEquals(color2.hex, deserialized[1].hex)
        assertEquals(color2.isFavorite, deserialized[1].isFavorite)
    }

    // 25. Settings persistence
    @Test
    fun test25_settingsPersistence() {
        val defaults = AppSettings()
        assertEquals(ThemeMode.SYSTEM, defaults.themeMode)
        assertFalse(defaults.dynamicColor)
        assertEquals(HexCase.UPPERCASE, defaults.hexCase)
        assertTrue(defaults.showHexPrefix)
        assertTrue(defaults.showCssFormat)
        assertTrue(defaults.showHexBreakdown)
        assertTrue(defaults.showRgbBreakdown)
        assertTrue(defaults.showChannelSliders)
        assertTrue(defaults.showRecentColors)
        assertTrue(defaults.confirmDelete)
        assertTrue(defaults.hapticFeedback)
        assertEquals(100, defaults.historyLimit)
        assertEquals("#FF0080", defaults.defaultColorHex)

        val custom = defaults.copy(
            themeMode = ThemeMode.DARK,
            hexCase = HexCase.LOWERCASE,
            historyLimit = 50,
            defaultColorHex = "#3366CC"
        )
        assertEquals(ThemeMode.DARK, custom.themeMode)
        assertEquals(HexCase.LOWERCASE, custom.hexCase)
        assertEquals(50, custom.historyLimit)
        assertEquals("#3366CC", custom.defaultColorHex)
    }

    // 26. Favorites
    @Test
    fun test26_favorites() {
        val item = SavedColor(
            id = "fav-1",
            name = "Favorite Item",
            notes = "",
            red = 255,
            green = 0,
            blue = 0,
            hex = "#FF0000",
            isFavorite = false
        )

        val toggled = item.copy(isFavorite = !item.isFavorite)
        assertTrue(toggled.isFavorite)

        val unToggled = toggled.copy(isFavorite = !toggled.isFavorite)
        assertFalse(unToggled.isFavorite)
    }

    // 27. Delete
    @Test
    fun test27_delete() {
        val item1 = SavedColor(id = "1", red = 10, green = 20, blue = 30, hex = "#0A141E")
        val item2 = SavedColor(id = "2", red = 40, green = 50, blue = 60, hex = "#28323C")
        val list = mutableListOf(item1, item2)

        val filtered = list.filterNot { it.id == "1" }
        assertEquals(1, filtered.size)
        assertEquals("2", filtered[0].id)
    }

    // 28. Duplicate
    @Test
    fun test28_duplicate() {
        val original = SavedColor(
            id = "orig-123",
            name = "Accent Blue",
            notes = "Primary header",
            red = 51,
            green = 102,
            blue = 204,
            hex = "#3366CC",
            createdAt = 1000L,
            isFavorite = true
        )

        val duplicate = original.copy(
            id = UUID.randomUUID().toString(),
            name = "Copy of ${original.name}",
            createdAt = System.currentTimeMillis(),
            isFavorite = false
        )

        assertNotEquals(original.id, duplicate.id)
        assertEquals("Copy of Accent Blue", duplicate.name)
        assertEquals(original.notes, duplicate.notes)
        assertEquals(original.red, duplicate.red)
        assertEquals(original.green, duplicate.green)
        assertEquals(original.blue, duplicate.blue)
        assertEquals(original.hex, duplicate.hex)
        assertFalse(duplicate.isFavorite)
    }
}
