package com.soneso.stellargate.domain.usecases

import android.util.Log
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.domain.data.UserSecurity
import com.soneso.stellargate.domain.util.*
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.mnemonic.WordList
import org.bouncycastle.util.encoders.Base64
import java.nio.ByteBuffer
import java.util.*

class UserSecurityHelper(private val pass: CharArray) {

    lateinit var mnemonicChars: CharArray
        private set

    fun generateUserSecurity(email: String): UserSecurity {

        val passwordKdfSalt = Cryptor.generateSalt()
        val derivedPassword = Cryptor.deriveKeyPbkdf2(passwordKdfSalt, pass)

        val wordListMasterKey = Cryptor.generateMasterKey()
        val (encryptedWordListMasterKey, wordListMasterKeyEncryptionIv) = Cryptor.encryptValue(wordListMasterKey, derivedPassword)

        val mnemonicMasterKey = Cryptor.generateMasterKey()
        val (encryptedMnemonicMasterKey, mnemonicMasterKeyEncryptionIv) = Cryptor.encryptValue(mnemonicMasterKey, derivedPassword)

        val wordList = mutableListOf<String>(*WordList.ENGLISH.words.toTypedArray()).shuffled()

        mnemonicChars = Wallet.generate24WordMnemonic()
        val mnemonicWords = String(mnemonicChars).split(" ")

        val mnemonicIndexes = ShortArray(mnemonicWords.size, {
            wordList.indexOf(mnemonicWords[it]).toShort()
        })
        val mnemonicByteList = mnemonicIndexes.flatMap { index: Short ->
            val bytes = ByteBuffer.allocate(2)
            bytes.putShort(index)
            bytes.array().asList()
        }
        val mnemonicBytes = ByteArray(2 * mnemonicIndexes.size, {
            mnemonicByteList[it]
        })
        val (encryptedMnemonic, mnemonicEncryptionIv) = Cryptor.encryptValue(mnemonicBytes, mnemonicMasterKey)

        val publicKeyIndex0 = Wallet.createKeyPair(mnemonicChars, null, 0).accountId
        val publicKeyIndex188 = Wallet.createKeyPair(mnemonicChars, null, 188).accountId

        val wordListStringBuilder = StringBuilder()
        wordList.forEach {
            wordListStringBuilder.append(it).append(",")
        }
        val wordListBytes = wordListStringBuilder.dropLast(1).padToBlocks(16).toByteArray()

        val (encryptedWordList, wordListEncryptionIv) = Cryptor.encryptValue(wordListBytes, wordListMasterKey)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "password kdf salt: ${Base64.toBase64String(passwordKdfSalt)}")
            Log.d(TAG, "derived password: ${Base64.toBase64String(derivedPassword)}")

            Log.d(TAG, "word list master key: ${Base64.toBase64String(wordListMasterKey)}")
            Log.d(TAG, "word list master key encryption iv: ${Base64.toBase64String(wordListMasterKeyEncryptionIv)}")
            Log.d(TAG, "encrypted word list master key: ${Base64.toBase64String(encryptedWordListMasterKey)}")

            Log.d(TAG, "mnemonic master key: ${Base64.toBase64String(mnemonicMasterKey)}")
            Log.d(TAG, "mnemonic master key encryption iv: ${Base64.toBase64String(mnemonicMasterKey)}")
            Log.d(TAG, "encrypted mnmemonic master key: ${Base64.toBase64String(encryptedMnemonicMasterKey)}")

            Log.d(TAG, "mnemonic: ${String(mnemonicChars)}")
            Log.d(TAG, "mnemonic indexes: ${Arrays.toString(mnemonicIndexes)}")

            Log.d(TAG, "mnemonic encryption iv: ${Base64.toBase64String(mnemonicEncryptionIv)}")
            Log.d(TAG, "encrypted mnemonic: ${Base64.toBase64String(encryptedMnemonic)}")

            Log.d(TAG, "public key index 0: $publicKeyIndex0")
            Log.d(TAG, "public key index 188: $publicKeyIndex188")

            logLongString("word list: ${wordListStringBuilder.dropLast(1)}")
            Log.d(TAG, "word list encryption iv: ${Base64.toBase64String(wordListEncryptionIv)}")
            logLongString("encrypted word list: ${Base64.toBase64String(encryptedWordList)}")
        }

        return UserSecurity(
                email,
                publicKeyIndex0,
                publicKeyIndex188,
                passwordKdfSalt,
                encryptedMnemonicMasterKey,
                mnemonicMasterKeyEncryptionIv,
                encryptedMnemonic,
                mnemonicEncryptionIv,
                encryptedWordListMasterKey,
                wordListMasterKeyEncryptionIv,
                encryptedWordList,
                wordListEncryptionIv
        )
    }

    fun decipherUserSecurity(userSecurity: UserSecurity): String? {

        val derivedPassword = Cryptor.deriveKeyPbkdf2(userSecurity.passwordKdfSalt, pass)

        val wordListMasterKey = Cryptor.decryptValue(derivedPassword, userSecurity.encryptedWordListMasterKey, userSecurity.wordListMasterKeyEncryptionIv)

        val wordListCsvBytes = Cryptor.decryptValue(wordListMasterKey, userSecurity.encryptedWordList, userSecurity.wordListEncryptionIv)
        val wordList = String(wordListCsvBytes).trim().split(",")

        val mnemonicMasterKey = Cryptor.decryptValue(derivedPassword, userSecurity.encryptedMnemonicMasterKey, userSecurity.mnemonicMasterKeyEncryptionIv)

        val mnemonicBytes = Cryptor.decryptValue(mnemonicMasterKey, userSecurity.encryptedMnemonic, userSecurity.mnemonicEncryptionIv)

        if (mnemonicBytes.size != 48) {
            return null
        }

        val mnemonicIndexes = ShortArray(mnemonicBytes.size / 2, { index ->
            val bf = ByteBuffer.wrap(mnemonicBytes, index * 2, 2)
            bf.short
        })
        val mnemonicBuilder = StringBuilder()
        mnemonicIndexes.forEach { index ->
            mnemonicBuilder.append(wordList[index.toInt()]).append(" ")
        }
        mnemonicChars = mnemonicBuilder.removeSuffix(" ").toCharArray()

        val publicKeyIndex0 = Wallet.createKeyPair(mnemonicChars, null, 0).accountId

        if (publicKeyIndex0 != userSecurity.publicKeyIndex0) {
            return null
        }

        return Wallet.createKeyPair(mnemonicChars, null, 188).accountId
    }


    companion object {

        const val TAG = "UserSecurityHelper"
    }
}