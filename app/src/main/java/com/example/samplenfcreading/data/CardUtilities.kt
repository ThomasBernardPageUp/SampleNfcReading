package com.example.samplenfcreading.data

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log

class CardUtilities {

    companion object {
        private val ISO_APDU_CHECK_GLOBAL_PIN: ByteArray = byteArrayOf(0x00, 0x20, 0x00, 0x01, 0x04)
        private val ISO_APDU_IS_APPLICATION_SELECT: ByteArray = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x0C, 0x0B, 0xF0.toByte(), 0x49, 0x61, 0x73, 0x45, 0x63, 0x63, 0x52, 0x6F, 0x6F, 0x74)
        private val ISO_APDU_GENERIC_APPLICATION_SELECT = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x0C, 0x0E, 0xE8.toByte(), 0x28, 0xBD.toByte(), 0x08, 0x0F, 0xD2.toByte(), 0x50, 0x47, 0x65, 0x6E, 0x65, 0x72, 0x69, 0x63)
        private val ISO_APDU_EF_B002_SELECT: ByteArray = byteArrayOf(0x00, 0xA4.toByte(), 0x02, 0x0C, 0x02, 0xB0.toByte(), 0x02)
        private val ISO_APDU_EF_B002_READ: ByteArray = byteArrayOf(0x00, 0xB0.toByte())
        private val ISO_APDU_BYTES_READ_ALL: ByteArray = byteArrayOf(0x00)

        private var iso: IsoDep? = null

        private suspend fun login(pin : String = "1234"): Boolean {
            val cmdPin: ByteArray = combineArray(ISO_APDU_CHECK_GLOBAL_PIN, pin.toByteArray())
            return transceiveApduCommand(cmdPin)
        }

        suspend fun downloadCertificate(tag : Tag): String {
            iso = IsoDep.get(tag)
            iso?.connect()
            Log.i("downloadCertificate", "[NFC] - Téléchargement du certificat de l'agent")

            var strCertificate = ""
            val strTemp = ""
            var again = true
            var cmdToGetCertificat: ByteArray?
            var i = 0
            var data: ByteArray?

            if (login()) {
                if (transceiveApduCommand(ISO_APDU_IS_APPLICATION_SELECT) && transceiveApduCommand(ISO_APDU_GENERIC_APPLICATION_SELECT) && transceiveApduCommand(ISO_APDU_EF_B002_SELECT)) {
                    do {
                        val strhex = String.format("%04x", 231 * i++)

                        cmdToGetCertificat = combineArray(ISO_APDU_EF_B002_READ,  hexStringToByteArray(strhex))
                        cmdToGetCertificat = combineArray(cmdToGetCertificat, ISO_APDU_BYTES_READ_ALL)

                        data = sendApduCommand(cmdToGetCertificat)

                        if (data.size == 2) {
                            if ((String.format(
                                    "%02x",
                                    data[data.size - 2]
                                ) + String.format("%02x", data[data.size - 1])) == "6b00"
                            ) {
                                again = false
                            }
                        } else {
                            for (k in 0 until data!!.size - 2) {
                                strCertificate += String.format("%02x", data!![k])
                            }
                        }
                        data = null
                    } while (again)

                    Log.d("downloadCertificate", "[NFC] - Certificat de l'agent téléchargé :\n$strCertificate")
                    return strCertificate
                }
            }

            Log.d("downloadCertificate", "[NFC] - Error during login")
            return "Error check logcat for more information"
        }

        private fun combineArray(first: ByteArray, second: ByteArray): ByteArray {
            val ret = ByteArray(first.size + second.size)
            System.arraycopy(first, 0, ret, 0, first.size)
            System.arraycopy(second, 0, ret, first.size, second.size)
            return ret
        }

        private fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] =
                    ((s[i].digitToIntOrNull(16) ?: -1 shl 4) + s[i + 1].digitToIntOrNull(16)!!
                        ?: -1).toByte()
                i += 2
            }
            return data
        }


        private fun transceiveApduCommand(cmd: ByteArray): Boolean {
            val response = sendApduCommand(cmd)
            return response.size >= 2 && response[0] == 0x90.toByte() && response[1] == 0x00.toByte()
        }

        private fun sendApduCommand(cmd: ByteArray): ByteArray {
            Log.d("sendApduCommand", "[NFC] - Envoi de la commande APDU :\n$cmd")

            if (iso?.isConnected == true){
                return runCatching {
                    val result = iso?.transceive(cmd)
                    Log.d("sendApduCommand", "[NFC] - Réponse de la carte :\n$result")
                    result
                }.getOrNull() ?: byteArrayOf()
            }

            Log.e("sendApduCommand", "[NFC] - Erreur lors de la connexion à la carte (ISO not connected)")
            return byteArrayOf()
        }
    }
}