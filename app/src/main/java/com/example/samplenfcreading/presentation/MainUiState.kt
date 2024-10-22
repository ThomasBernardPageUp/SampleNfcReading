package com.example.samplenfcreading.presentation

data class MainUiState(
    val tagId : String = "",
    val techList : List<String> = emptyList(),

    val loading : Boolean = false,
    val reading : Boolean = false,

    val certificateResult : String = "",
    val downloadingCertificate: Boolean = false,
)