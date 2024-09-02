package com.example.samplenfcreading.presentation

data class MainUiState(
    val message : String = "",

    val tagId : String = "",

    val loading : Boolean = false,
    val reading : Boolean = false
)