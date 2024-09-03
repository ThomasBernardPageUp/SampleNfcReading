package com.example.samplenfcreading

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
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

    override fun onResume() {
        super.onResume()
        val adapter: NfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE)
        adapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }
    override fun onPause() {
        super.onPause()
        val adapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(this)
        adapter?.disableForegroundDispatch(this)
    }
}