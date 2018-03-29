package com.soneso.stellargate.persistence

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.soneso.stellargate.BuildConfig
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyHelper {

    companion object {

        fun appSecretKey() = getSecretKey(BuildConfig.APPLICATION_ID).encoded.toString(charset("UTF-8"))

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

        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}