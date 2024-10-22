package com.example.samplenfcreading.domain.exceptions

data class InvalidByteArrayException(override val message: String = "Invalid byte array") : Exception(message) {
}