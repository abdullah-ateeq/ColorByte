package com.example.colorbyte.data.model

import org.json.JSONObject
import java.util.UUID

data class SavedColor(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val notes: String = "",
    val red: Int,
    val green: Int,
    val blue: Int,
    val hex: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = name.trim().ifEmpty { "Untitled Color" }

    val rgb: RgbColor
        get() = RgbColor(red, green, blue)

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            put("notes", notes)
            put("red", red)
            put("green", green)
            put("blue", blue)
            put("hex", hex)
            put("createdAt", createdAt)
            put("isFavorite", isFavorite)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): SavedColor {
            return SavedColor(
                id = json.optString("id", UUID.randomUUID().toString()),
                name = json.optString("name", ""),
                notes = json.optString("notes", ""),
                red = json.getInt("red"),
                green = json.getInt("green"),
                blue = json.getInt("blue"),
                hex = json.getString("hex"),
                createdAt = json.optLong("createdAt", System.currentTimeMillis()),
                isFavorite = json.optBoolean("isFavorite", false)
            )
        }
    }
}
