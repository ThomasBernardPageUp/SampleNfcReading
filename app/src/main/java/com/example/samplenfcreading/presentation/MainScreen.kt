package com.example.samplenfcreading.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samplenfcreading.MainActivity
import com.example.samplenfcreading.R
import com.example.samplenfcreading.presentation.components.MenuItem

@Composable
fun MainScreen(uiState : MainUiState, onEvent : (MainEvent) -> Unit) {
    Scaffold {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val context = LocalContext.current

                MenuItem(
                    image = R.drawable.start,
                    enabled = !uiState.reading,
                    title = R.string.start_reading,
                    onClick = {
                        (context as? MainActivity)?.let { activity ->
                            onEvent(MainEvent.OnStartNFCReading(activity))
                        }
                    }
                )

                MenuItem(
                    image = R.drawable.restart,
                    title = R.string.restart_reader,
                    enabled = uiState.reading,
                    onClick = {
                        (context as? MainActivity)?.let { activity ->
                            onEvent(MainEvent.OnRestartNFCReading(activity))
                        }
                    }
                )

                MenuItem(
                    image = R.drawable.pause,
                    title = R.string.stop_reading,
                    enabled = uiState.reading,
                    onClick = {
                        (context as? MainActivity)?.let { activity ->
                            onEvent(MainEvent.OnStopNFCReading(activity))
                        }
                    }
                )

                Text(
                    text = stringResource(id = R.string.tag_id, uiState.tagId)
                )

                Text(
                    text = stringResource(id = R.string.tech_list, uiState.techList.joinToString(", "))
                )

                Text(
                    text = stringResource(id = R.string.certificate, uiState.certificate)
                )
            }

            if (uiState.downloadingCertificate) {
                LinearProgressIndicator(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview
private fun MainScreenPreview() {
    MainScreen(MainUiState(), {})
}

// NOT WORKING
//fun requestDisableBatteryOptimization(activity: ComponentActivity) {
//    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).also {
//        it.data = android.net.Uri.parse("package:${activity.packageName}")
//        activity.startActivity(it)
//    }
//}