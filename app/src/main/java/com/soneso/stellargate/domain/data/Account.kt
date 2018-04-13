package com.soneso.stellargate.domain.data

class Account(
        val publicKeyIndex0: String,
        val publicKeyIndex188: String,
        val passwordSalt: ByteArray,
        val encryptedMasterKey: ByteArray,
        val masterKeyIV: ByteArray,
        val encryptedMnemonic: ByteArray,
        val mnemonicIV: ByteArray
) {
    var email = ""
}