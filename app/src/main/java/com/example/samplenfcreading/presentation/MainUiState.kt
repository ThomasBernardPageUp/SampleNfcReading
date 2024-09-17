package com.example.samplenfcreading.presentation

import java.security.cert.Certificate

data class MainUiState(
    val tagId : String = "",
    val techList : List<String> = emptyList(),

    val loading : Boolean = false,
    val reading : Boolean = false,

    val certificate : String = "",
    val downloadingCertificate: Boolean = false,
)