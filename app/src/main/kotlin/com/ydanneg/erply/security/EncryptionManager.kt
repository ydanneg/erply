package com.ydanneg.erply.security

interface EncryptionManager {
    suspend fun encryptText(keyAlias: String, data: String): EncryptedData

    suspend fun decryptText(keyAlias: String, encrypted: ByteArray, iv: ByteArray): String
}
