@file:Suppress("DEPRECATION")

package com.soneso.stellargate.persistence

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

class SgCipher(context: Context, private val alias: String) {

    private val keyStore = try {
        KeyStore.getInstance(ANDROID_KEYSTORE)
    } catch (e: Exception) {
        null
    }

    init {
        keyStore?.load(null)
        createNewKeys(context)
    }

    private fun createNewKeys(context: Context) {
        try {
            // Create new key if needed
            if (keyStore?.containsAlias(alias) == false) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 1)
                val spec = KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(X500Principal("CN=Stellargate, O=Soneso"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.time)
                        .setEndDate(end.time)
                        .build()
                val generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEYSTORE)
                generator.initialize(spec)

                generator.generateKeyPair()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.javaClass.name, e)
        }
    }

    fun deleteKey() {
        try {
            keyStore?.deleteEntry(alias)
        } catch (e: KeyStoreException) {
            Log.e(TAG, e.javaClass.name, e)
        }
    }

    fun encryptText(text: String): String? {
        try {
            val privateKeyEntry = keyStore?.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey


            if (text.isEmpty()) {
                return null
            }

            val inCipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, inCipher)
            cipherOutputStream.write(text.toByteArray(charset("UTF-8")))
            cipherOutputStream.close()

            val bytes = outputStream.toByteArray()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            return null
        }
    }

    fun decryptText(text: String): String? {
        return try {
            val privateKeyEntry = keyStore?.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val privateKey = privateKeyEntry.privateKey

            val output = Cipher.getInstance(CIPHER_TRANSFORMATION)
            output.init(Cipher.DECRYPT_MODE, privateKey)

            val cipherInputStream = CipherInputStream(
                    ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)),
                    output
            )
            cipherInputStream.bufferedReader(charset("UTF-8")).use { it.readText() }

        } catch (e: Exception) {
            Log.e(TAG, e.javaClass.name, e)
            null
        }
    }

    companion object {
        const val TAG = "SgCipher"
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }
}