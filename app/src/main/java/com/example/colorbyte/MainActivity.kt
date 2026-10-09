package com.example.colorbyte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.colorbyte.data.repository.ColorByteRepository
import com.example.colorbyte.ui.ColorByteApp
import com.example.colorbyte.ui.theme.ColorByteTheme
import com.example.colorbyte.ui.viewmodel.ColorByteViewModel
import com.example.colorbyte.ui.viewmodel.ColorByteViewModelFactory

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        ColorByteRepository(applicationContext)
    }

    private val viewModel by viewModels<ColorByteViewModel> {
        ColorByteViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            ColorByteTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                ColorByteApp(viewModel = viewModel)
            }
        }
    }
}