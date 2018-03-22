package com.soneso.stellargate.domain.util

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Util class.
 * Created by cristi.paval on 3/22/18.
 */
class CryptoUtil {

    companion object {

        private val random = SecureRandom()

        fun decryptValue(encryptedValue: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
            try {
                val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
                val ivParams = IvParameterSpec(iv)
                val secretKey = SecretKeySpec(key, "AES")
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams)
                return cipher.doFinal(encryptedValue)
            } catch (e: Exception) {
                throw EncryptionException(e)
            }
        }

        fun encryptValue(plainValue: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
            try {
                val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
                val ivParams = IvParameterSpec(iv)
                val secretKey = SecretKeySpec(key, "AES")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams)
                return cipher.doFinal(plainValue) // encrypt.
            } catch (e: Exception) {
                throw EncryptionException(e)
            }
        }

        // generates a new IV.
        fun generateIv(): ByteArray {
            val b = ByteArray(IV_LENGTH)
            random.nextBytes(b)
            return b
        }

        // generates a new SALT
        fun generateSalt(): ByteArray {
            val b = ByteArray(SALT_LENGTH)
            random.nextBytes(b)
            return b
        }

        // generates a new random master key.
        fun generateMasterKey(): ByteArray {

            val keyGen: KeyGenerator
            try {
                keyGen = KeyGenerator.getInstance("AES")
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }

            keyGen.init(MASTER_KEY_LENGTH)
            return keyGen.generateKey().getEncoded()
        }

        fun deriveKeyPbkdf2(password: CharArray, salt: ByteArray): ByteArray {
            Objects.requireNonNull(password)
            val keySpec = PBEKeySpec(password, salt, PBE_ITERATION_COUNT, MASTER_KEY_LENGTH)
            val algorithm = PBKDF2_DERIVATION_ALGORITHM //TODO check best algorithm
            //        if (Build.VERSION.RELEASE != null && "2.2".equalsIgnoreCase(Build.VERSION.RELEASE.trim())) {
            //            algorithm = API8_PBKDF2_DERIVATION_ALGORITHM;
            //        }
            try {
                val keyFactory = SecretKeyFactory.getInstance(algorithm)
                return keyFactory.generateSecret(keySpec).getEncoded()
            } catch (e: Exception) {
                throw EncryptionException(e)
            }

        }

        fun padCharsTo16BytesFormat(source: CharArray): CharArray {
            //String result = pSource;
            val size = 16
            val x = source.size % size
            val extensionLength = size - x

            val result = Arrays.copyOf(source, source.size + extensionLength)

            for (i in source.size until result.size) {
                result[i] = EMPTY_SPACE.toChar()
            }

            return result
        }

        private const val EMPTY_SPACE = 32

        private const val MASTER_KEY_LENGTH = 256 // 128 if invalid key length
        private const val IV_LENGTH = 16
        private const val SALT_LENGTH = MASTER_KEY_LENGTH / 8
        private const val PBE_ITERATION_COUNT = 10000
        private const val PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val CIPHER_ALGORITHM = "AES/CBC/NoPadding"
    }

}