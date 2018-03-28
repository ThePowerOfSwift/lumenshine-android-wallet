package com.soneso.stellargate.persistence.secureprefs

import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 *  _____        _____                  _
 * |  __ \      / ____|                | |
 * | |  | | ___| |     _ __ _   _ _ __ | |_ ___  _ __
 * | |  | |/ _ \ |    | '__| | | | '_ \| __/ _ \| '__|
 * | |__| |  __/ |____| |  | |_| | |_) | || (_) | |
 * |_____/ \___|\_____|_|   \__, | .__/ \__\___/|_|
 *                           __/ | |
 *                          |___/|_|
 */
internal class DeCryptor {

    private var keyStore: KeyStore

    init {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
    }

    fun decryptData(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String {

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)

        return String(cipher.doFinal(encryptedData), Charset.forName("UTF-8"))
    }

    private fun getSecretKey(alias: String): SecretKey {
        return (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    companion object {

        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}