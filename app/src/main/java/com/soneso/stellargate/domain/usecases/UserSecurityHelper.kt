package com.soneso.stellargate.domain.usecases

import android.util.Log
import com.soneso.stellargate.domain.data.UserSecurity
import com.soneso.stellargate.domain.util.Cryptor
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.mnemonic.WordList
import org.bouncycastle.util.encoders.Base64
import java.nio.ByteBuffer

class UserSecurityHelper(private val pass: CharArray) {

    private val passwordKdfSalt = Cryptor.generateSalt()
    private val derivedPassword = Cryptor.deriveKeyPbkdf2(passwordKdfSalt, pass)
    private val mnemonicMasterKey = Cryptor.generateMasterKey()
    private val wordListMasterKey = Cryptor.generateMasterKey()
    private val encryptedMnemonicMasterKey: ByteArray
    private val mnemonicMasterKeyEncryptionIv: ByteArray
    private val encryptedWordListMasterKey: ByteArray
    private val wordListMasterKeyIv: ByteArray
    private val mnemonicChars = Wallet.generate24WordMnemonic()
    private val mnemonicWords = String(mnemonicChars).split(" ")
    private val mnemonicIndexes = ShortArray(mnemonicWords.size, {
        wordList.indexOf(mnemonicWords[it]).toShort()
    })
    private val mnemonicBytes: ByteArray
    private val encryptedMnemonic: ByteArray
    private val mnemonicEncryptionIv: ByteArray
    private val wordList = mutableListOf<String>(*WordList.ENGLISH.words.toTypedArray()).shuffled()
    private val wordListBytes: ByteArray
    private val encryptedWordList: ByteArray
    private val wordListEncryptionIv: ByteArray

    init {
        val (emmKey, mmkIv) = Cryptor.encryptValue(mnemonicMasterKey, derivedPassword)
        encryptedMnemonicMasterKey = emmKey
        mnemonicMasterKeyEncryptionIv = mmkIv

        val (ewlmKey, wlmkIv) = Cryptor.encryptValue(wordListMasterKey, derivedPassword)
        encryptedWordListMasterKey = ewlmKey
        wordListMasterKeyIv = wlmkIv

        val mnemonicByteList = mnemonicIndexes.flatMap { index: Short ->
            val bytes = ByteBuffer.allocate(2)
            bytes.putShort(index)
            bytes.array().asList()
        }
        mnemonicBytes = ByteArray(2 * mnemonicIndexes.size, {
            mnemonicByteList[it]
        })

        val (em, mi) = Cryptor.encryptValue(mnemonicBytes, mnemonicMasterKey)
        encryptedMnemonic = em
        mnemonicEncryptionIv = mi

        val wordListStringBuilder = StringBuilder()
        wordList.forEach {
            wordListStringBuilder.append(it).append(",")
        }
        wordListBytes = wordListStringBuilder.removeSuffix(",").toString().toByteArray()

        val (ewl, wli) = Cryptor.encryptValue(mnemonicBytes, mnemonicMasterKey)
        encryptedWordList = ewl
        wordListEncryptionIv = wli
    }

    fun generateUserSecurity(email: String): UserSecurity {

        // cristi.paval, 3/23/18 - generate public keys
        val publicKeyIndex0 = Wallet.createKeyPair(mnemonicChars, null, 0).accountId

        val publicKeyIndex188 = Wallet.createKeyPair(mnemonicChars, null, 188).accountId

        return UserSecurity(
                email,
                publicKeyIndex0,
                publicKeyIndex188,
                passwordKdfSalt,
                encryptedMnemonicMasterKey,
                mnemonicMasterKeyEncryptionIv,
                encryptedMnemonic,
                mnemonicEncryptionIv,
                encryptedWordList,
                wordListEncryptionIv
        )
    }

    @Suppress("unused")
    fun logSecurityData() {
        Log.d(AuthUseCases.TAG, "password: ${String(pass)}")

        val encodedPassSalt = Base64.toBase64String(passwordKdfSalt)
        Log.d(AuthUseCases.TAG, "kdf salt: $encodedPassSalt \t\t\t\tlength: ${encodedPassSalt.length}")

        val encodedKdfPass = Base64.toBase64String(derivedPassword)
        Log.d(AuthUseCases.TAG, "kdf password: $encodedKdfPass \t\t\t\tlength: ${encodedKdfPass.length}")

        val encodedMnemonicMasterKey = Base64.toBase64String(mnemonicMasterKey)
        Log.d(AuthUseCases.TAG, "master key: $encodedMnemonicMasterKey \t\t\t\tlength: ${encodedMnemonicMasterKey.length}")

//        val encryptedMasterKeyEncoded = Base64.toBase64String(encryptedMasterKey)
//        Log.d(AuthUseCases.TAG, "encrypted master key: $encryptedMasterKeyEncoded \t\t\t\tlength: ${encryptedMasterKeyEncoded.length}")

        val masterKeyIvEncoded = Base64.toBase64String(mnemonicMasterKeyEncryptionIv)
        Log.d(AuthUseCases.TAG, "master key iv: $masterKeyIvEncoded \t\t\t\tlength: ${masterKeyIvEncoded.length}")

        Log.d(AuthUseCases.TAG, "mnemonic: ${String(mnemonicChars)}")

//        val encryptedMnemonicEncoded = Base64.toBase64String(encryptedMnemonic)
//        Log.d(AuthUseCases.TAG, "encrypted mnemonic: $encryptedMnemonicEncoded length: ${encryptedMnemonicEncoded.length}")

//        val mnemonicIvEncoded = Base64.toBase64String(userSecurity.mnemonicEncryptionIv)
//        Log.d(AuthUseCases.TAG, "mnemonic iv: $mnemonicIvEncoded \t\t\t\tlength: ${mnemonicIvEncoded.length}")
    }
}