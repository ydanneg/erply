package com.ydanneg.erply.security

import android.content.Context
import android.content.pm.PackageManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_CBC
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class EncryptedData(
    val iv: ByteArray,
    val data: ByteArray
)

class EncryptionManager @Inject constructor(@ApplicationContext val context: Context) {
    suspend fun encryptText(keyAlias: String, data: String): EncryptedData =
        withContext(Dispatchers.IO) {
            Cipher.getInstance(TRANSFORMATION).run {
                init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey(keyAlias))
                EncryptedData(
                    iv = iv,
                    data = doFinal(data.toByteArray(Charsets.UTF_8))
                )
            }
        }

    suspend fun decryptText(keyAlias: String, encryptedData: ByteArray, iv: ByteArray): String =
        withContext(Dispatchers.IO) {
            Cipher.getInstance(TRANSFORMATION).run {
                init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(keyAlias), IvParameterSpec(iv))
                doFinal(encryptedData).toString(Charsets.UTF_8)
            }
        }

    private fun keyStore() = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    private fun getOrCreateSecretKey(keyAlias: String): SecretKey =
        getSecretKey(keyAlias) ?: KeyGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE).apply {
            init(
                KeyGenParameterSpec
                    .Builder(keyAlias, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setKeySize(KEY_SIZE)
                    .setEncryptionPaddings(PADDING)
                    .apply {
                        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)) {
                            setIsStrongBoxBacked(true)
                        }
                    }
                    .build()
            )
        }.generateKey()

    private fun getSecretKey(keyAlias: String) = keyStore().getKey(keyAlias, charArrayOf()) as? SecretKey

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"

        private const val ALGORITHM = KEY_ALGORITHM_AES
        private const val BLOCK_MODE = BLOCK_MODE_CBC
        private const val PADDING = ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"//NON-NLS
        private const val KEY_SIZE = 256
    }
}
