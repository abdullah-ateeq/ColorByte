package com.example.colorbyte.data.model

data class ColorByteInput(
    val rgb: RgbColor,
    val hex: String,
    val name: String = "",
    val notes: String = ""
)
