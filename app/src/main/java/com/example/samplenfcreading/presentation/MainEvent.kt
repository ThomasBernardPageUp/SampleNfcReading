package com.example.samplenfcreading.presentation

import android.app.Activity

sealed class MainEvent {
    data class OnStartNFCReading(val activity : Activity) : MainEvent()
    data class OnStopNFCReading(val activity : Activity) : MainEvent()
    data class OnRestartNFCReading(val activity : Activity) : MainEvent()
}