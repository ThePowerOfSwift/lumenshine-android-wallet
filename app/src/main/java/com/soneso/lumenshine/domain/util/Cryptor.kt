package com.soneso.lumenshine.domain.util


import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * This class provides kdf master key encryption.
 *
 *
 * By using the password a derived key will be generated. Also a random master key will be generated.
 * The key derived from the password is used to encrypt/decrypt the master key. After encryption, the encrypted
 * master key is stored hidden in a file in the file system.
 * You can also setup this class by using a backup password. Use the backup password to recover the master key if
 * you loose the main password.
 *
 *
 * NEVER ever store the password or backup password on disk! These are only known by the user.
 *
 * @author Christian Rogobete
 */
object Cryptor {

    const val TAG = "Cryptor"
    private const val KEY_LENGTH = 256
    private const val BITS_IN_BYTES = 8
    private const val SALT_LENGTH = KEY_LENGTH / BITS_IN_BYTES
    private const val PBE_ITERATION_COUNT = 20000
    private const val PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1"
    private const val CIPHER_ALGORITHM = "AES/CBC/NoPadding"

    private val random = SecureRandom()

    // derives a key from the given password to be used to encrypt the master key.
    @Throws(GeneralSecurityException::class)
    fun deriveKeyPbkdf2(pSalt: ByteArray, pPassword: CharArray): ByteArray {

        val keySpec = PBEKeySpec(pPassword, pSalt, PBE_ITERATION_COUNT, KEY_LENGTH)
        val algorithm = PBKDF2_DERIVATION_ALGORITHM
        val keyFactory = SecretKeyFactory.getInstance(algorithm)
        return keyFactory.generateSecret(keySpec).encoded
    }

    fun encryptValue(value: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val ivParams = IvParameterSpec(iv)


        val secretKey = SecretKeySpec(key, CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams)
        return cipher.doFinal(value) // encrypt
    }

    //decrypts a master key
    @Throws(GeneralSecurityException::class)
    fun decryptValue(encryptedValue: ByteArray, key: ByteArray, encryptionIV: ByteArray): ByteArray {


        // Decrypt the master key using the loaded data and given password.

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val secretKey = SecretKeySpec(key, CIPHER_ALGORITHM)
        val ivParams = IvParameterSpec(encryptionIV)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams)
        return cipher.doFinal(encryptedValue)
    }

    fun applyPadding(data: ByteArray, blockSize: Int = 16): ByteArray {

        var resultSize = data.size + 1

        if (resultSize % blockSize != 0) {
            val blockCount = resultSize / blockSize + 1
            resultSize = blockSize * blockCount
        }

        val paddedData = ByteArray(resultSize)
        paddedData.forEachIndexed { index, _ ->
            when {
                index < data.size -> {
                    paddedData[index] = data[index]
                }
                index == data.size -> {
                    paddedData[index] = 0x80.toByte()
                }
                else -> {
                    paddedData[index] = 0x00.toByte()
                }
            }
        }

        return paddedData
    }

    fun removePadding(paddedData: ByteArray): ByteArray {

        return paddedData
                // cristi.paval, 4/18/18 - remove 0x00 bytes
                .dropLastWhile {
                    it == 0x00.toByte()
                }
                // cristi.paval, 4/18/18 - remove delimiter byte 0x80
                .dropLast(1)
                .toByteArray()
    }

    fun generateIv(): ByteArray {

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val b = ByteArray(cipher.blockSize)
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
    @Throws(GeneralSecurityException::class)
    fun generateMasterKey(): ByteArray {

        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(KEY_LENGTH)
        return keyGen.generateKey().encoded

    }
}
