package com.example.samplenfcreading.presentation

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private var nfcAdapter : NfcAdapter? = null
    private var readingJob : Job? = null
    private val dispatcherIO  = Dispatchers.IO
    private val flags = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
    private val options = Bundle().apply {  }

    fun onEvent(event: MainEvent) {
        try {
            when (event) {
                is MainEvent.OnStartNFCReading -> {
                    nfcAdapter = NfcAdapter.getDefaultAdapter(event.activity)

                    if (nfcAdapter?.isEnabled == true){
                        Log.e("MainViewModel", "NFC is enabled, disable it before enabling reader mode")
                        nfcAdapter?.disableReaderMode(event.activity)
                    }

                    nfcAdapter?.enableReaderMode(event.activity, { onTagDetected(it) }, flags, options)
                    _uiState.update { it.copy(reading = true, tagId = "", message = "") }
                }
                is MainEvent.OnRestartNFCReading -> {
                    Log.d("MainViewModel", "onRestartReader")
                    if (readingJob?.isActive == true){
                        Log.i("MainViewModel", "Reading job is active cannot restart reader")
                        return
                    }

                    nfcAdapter?.disableReaderMode(event.activity)
                    nfcAdapter?.enableReaderMode(event.activity, { onTagDetected(it) }, flags, options)
                    _uiState.update { it.copy(reading = true, tagId = "", message = "") }
                }
                is MainEvent.OnStopNFCReading -> {
                    Log.d("MainViewModel", "onStopReading")

                    if (readingJob?.isActive == true){
                        Log.i("MainViewModel", "Reading job is active cannot stop reader")
                        return
                    }

                    nfcAdapter?.disableForegroundDispatch(event.activity)
                    nfcAdapter?.disableReaderMode(event.activity)
                    nfcAdapter = null
                    _uiState.update { it.copy(reading = false, tagId = "", message = "") }
                }
            }
        }
        catch (e : Exception){
            _uiState.update { it.copy(message = e.message ?: "An error occurred", reading = false) }
        }
    }

    private fun onTagDetected(tag : Tag){
        if (readingJob?.isActive == true){
            Log.i("MainViewModel", "Reading job is active, ignoring this tag")
            return
        }

        readingJob = viewModelScope.launch(dispatcherIO) {
            onTagDiscovered(tag)
        }
    }

    private suspend fun onTagDiscovered(tag: Tag){
        Log.d("MainViewModel", "onTagDiscovered $tag")
        _uiState.update { it.copy(tagId = tag.id.toString()) }
    }
}