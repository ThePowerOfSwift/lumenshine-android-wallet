package com.soneso.lumenshine.networking.dto.auth

import org.bouncycastle.util.encoders.Base64

class ChangePasswordRequest {

    var publicKeyIndex188 = ""

    var passwordKdfSalt = ""

    var encryptedMnemonicMasterKey = ""
    var mnemonicMasterKeyEncryptionIv = ""

    var encryptedWordListMasterKey = ""
    var wordListMasterKeyEncryptionIv = ""

    fun setPasswordKdfSalt(salt: ByteArray) {
        passwordKdfSalt = Base64.toBase64String(salt)
    }

    fun setEncryptedMnemonicMasterKey(masterKey: ByteArray) {
        encryptedMnemonicMasterKey = Base64.toBase64String(masterKey)
    }

    fun setMnemonicMasterKeyEncryptionIv(encryptionIv: ByteArray) {
        mnemonicMasterKeyEncryptionIv = Base64.toBase64String(encryptionIv)
    }

    fun setEncryptedWordListMasterKey(masterKey: ByteArray) {
        encryptedWordListMasterKey = Base64.toBase64String(masterKey)
    }

    fun setWordListMasterKeyEncryptionIv(encryptionIv: ByteArray) {
        wordListMasterKeyEncryptionIv = Base64.toBase64String(encryptionIv)
    }
}