package com.example.colorbyte.data.model

data class RgbColor(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    init {
        require(red in 0..255) { "Red channel must be in 0..255, got $red" }
        require(green in 0..255) { "Green channel must be in 0..255, got $green" }
        require(blue in 0..255) { "Blue channel must be in 0..255, got $blue" }
    }
}
