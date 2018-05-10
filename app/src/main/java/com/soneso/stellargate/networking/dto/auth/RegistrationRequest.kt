package com.soneso.stellargate.networking.dto.auth

import org.bouncycastle.util.encoders.Base64

class RegistrationRequest {

    var email = ""
    var publicKeyIndex0 = ""
    var publicKeyIndex188 = ""

    var passwordKdfSalt = ""

    var encryptedMnemonicMasterKey = ""
    var mnemonicMasterKeyEncryptionIv = ""

    var encryptedMnemonic = ""
    var mnemonicEncryptionIv = ""

    var encryptedWordListMasterKey = ""
    var wordListMasterKeyEncryptionIv = ""

    var encryptedWordList = ""
    var wordListEncryptionIv = ""

    var countryCode: String? = null

    fun setPasswordKdfSalt(salt: ByteArray) {
        passwordKdfSalt = Base64.toBase64String(salt)
    }

    fun setEncryptedMnemonicMasterKey(masterKey: ByteArray) {
        encryptedMnemonicMasterKey = Base64.toBase64String(masterKey)
    }

    fun setMnemonicMasterKeyEncryptionIv(encryptionIv: ByteArray) {
        mnemonicMasterKeyEncryptionIv = Base64.toBase64String(encryptionIv)
    }

    fun setEncryptedMnemonic(mnemonic: ByteArray) {
        encryptedMnemonic = Base64.toBase64String(mnemonic)
    }

    fun setMnemonicEncryptionIv(encryptionIv: ByteArray) {
        mnemonicEncryptionIv = Base64.toBase64String(encryptionIv)
    }

    fun setEncryptedWordListMasterKey(masterKey: ByteArray) {
        encryptedWordListMasterKey = Base64.toBase64String(masterKey)
    }

    fun setWordListMasterKeyEncryptionIv(encryptionIv: ByteArray) {
        wordListMasterKeyEncryptionIv = Base64.toBase64String(encryptionIv)
    }

    fun setEncryptedWordList(wordList: ByteArray) {
        encryptedWordList = Base64.toBase64String(wordList)
    }

    fun setWordListEncryptionIv(encryptionIv: ByteArray) {
        wordListEncryptionIv = Base64.toBase64String(encryptionIv)
    }
}