package com.soneso.stellargate.persistence.secureprefs

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 *  ______        _____                  _
 * |  ____|      / ____|                | |
 * | |__   _ __ | |     _ __ _   _ _ __ | |_ ___  _ __
 * |  __| | '_ \| |    | '__| | | | '_ \| __/ _ \| '__|
 * | |____| | | | |____| |  | |_| | |_) | || (_) | |
 * |______|_| |_|\_____|_|   \__, | .__/ \__\___/|_|
 *                            __/ | |
 *                           |___/|_|
 */

internal class EnCryptor {

    fun encryptText(alias: String, iv: ByteArray, textToEncrypt: String): ByteArray {

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivParams = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias), ivParams)
        return cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))
    }

    private fun getSecretKey(alias: String): SecretKey {

        val keyGenerator: KeyGenerator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            keyGenerator.init(spec)
        } else {
            keyGenerator = KeyGenerator.getInstance(ANDROID_KEY_STORE)
            keyGenerator.init(256)
        }
        return keyGenerator.generateKey()
    }

    companion object {

        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}
