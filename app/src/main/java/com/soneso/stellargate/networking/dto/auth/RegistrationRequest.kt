package com.soneso.stellargate.networking.dto.auth

import org.bouncycastle.util.encoders.Base64

class RegistrationRequest {

    var email = ""
    var publicKeyIndex0 = ""
    var publicKeyIndex188 = ""
    var passwordKdfSalt = ""
    var encryptedMasterKey = ""
    var masterKeyEncryptionIv = ""
    var encryptedMnemonic = ""
    var mnemonicEncryptionIv = ""
    var countryCode: String? = null

    fun setPasswordKdfSalt(salt: ByteArray) {
        passwordKdfSalt = Base64.toBase64String(salt)
    }

    fun setEncryptedMasterKey(masterKey: ByteArray) {
        encryptedMasterKey = Base64.toBase64String(masterKey)
    }

    fun setMasterKeyEncryptionIv(encryptionIv: ByteArray) {
        masterKeyEncryptionIv = Base64.toBase64String(encryptionIv)
    }

    fun setEncryptedMnemonic(mnemonic: ByteArray) {
        encryptedMnemonic = Base64.toBase64String(mnemonic)
    }

    fun setMnemonicEncryptionIv(encryptionIv: ByteArray) {
        mnemonicEncryptionIv = Base64.toBase64String(encryptionIv)
    }
}