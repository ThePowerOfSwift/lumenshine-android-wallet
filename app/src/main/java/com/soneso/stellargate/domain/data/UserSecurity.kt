package com.soneso.stellargate.domain.data

class UserSecurity(
        val publicKeyIndex0: String,
        var publicKeyIndex188: String,
        val passwordKdfSalt: ByteArray,
        val encryptedMasterKey: ByteArray,
        val masterKeyEncryptionIv: ByteArray,
        val encryptedMnemonic: ByteArray,
        val mnemonicEncryptionIv: ByteArray
) {
    companion object {
        fun mockInstance() = UserSecurity(
                "",
                "",
                ByteArray(0),
                ByteArray(0),
                ByteArray(0),
                ByteArray(0),
                ByteArray(0)
        )
    }
}