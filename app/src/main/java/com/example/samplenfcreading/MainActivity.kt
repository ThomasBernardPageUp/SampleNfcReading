package com.example.samplenfcreading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.samplenfcreading.presentation.MainScreen
import com.example.samplenfcreading.presentation.MainViewModel
import com.example.samplenfcreading.ui.theme.SampleNFCReadingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleNFCReadingTheme {
                val viewModel : MainViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MainScreen(uiState = uiState, onEvent = viewModel::onEvent)
            }
        }
    }
}