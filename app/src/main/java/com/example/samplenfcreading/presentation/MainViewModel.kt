package com.example.samplenfcreading.presentation

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samplenfcreading.data.CardUtilities
import com.example.samplenfcreading.domain.exceptions.InvalidByteArrayException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private var nfcAdapter : NfcAdapter? = null
    private var readingJob : Job? = null
    private val dispatcherIO  = Dispatchers.IO
    private val flags = NfcAdapter.FLAG_READER_NFC_A or
//            NfcAdapter.FLAG_READER_NFC_B or
//            NfcAdapter.FLAG_READER_NFC_F or
//            NfcAdapter.FLAG_READER_NFC_V or
            NfcAdapter.FLAG_READER_NFC_BARCODE or
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

    private val options = Bundle().apply {  }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnStartNFCReading -> {
                nfcAdapter = NfcAdapter.getDefaultAdapter(event.activity)

                if (nfcAdapter?.isEnabled == true){
                    Log.e("MainViewModel", "NFC is enabled, disable it before enabling reader mode")
                    nfcAdapter?.disableReaderMode(event.activity)
                }

                _uiState.update { it.copy(reading = true, tagId = "", techList = emptyList(), certificateResult = "") }
                nfcAdapter?.enableReaderMode(event.activity, { onTagDetected(it) }, flags, options)
            }
            is MainEvent.OnRestartNFCReading -> {
                Log.d("MainViewModel", "onRestartReader")
                if (readingJob?.isActive == true){
                    Log.i("MainViewModel", "Reading job is active cannot restart reader")
                    return
                }

                nfcAdapter?.disableReaderMode(event.activity)
                _uiState.update { it.copy(reading = true, tagId = "", techList = emptyList(), certificateResult = "") }
                nfcAdapter?.enableReaderMode(event.activity, { onTagDetected(it) }, flags, options)
            }
            is MainEvent.OnStopNFCReading -> {
                Log.d("MainViewModel", "onStopReading")

                if (readingJob?.isActive == true){
                    Log.i("MainViewModel", "Reading job is active cannot stop reader")
                    return
                }

                nfcAdapter?.disableReaderMode(event.activity)
                nfcAdapter = null
                _uiState.update { it.copy(reading = false, tagId = "", techList = emptyList(), certificateResult = "") }
            }
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
        _uiState.update { it.copy(tagId = tag.id.toString(), techList = tag.techList.map { it.toString() }) }


        _uiState.update { it.copy(downloadingCertificate = true) }
        withContext(Dispatchers.IO){
            val certificate = CardUtilities.downloadCertificate(tag)
            _uiState.update { it.copy(downloadingCertificate = false, certificateResult = certificate) }
        }
    }
}

