package com.soneso.stellargate.domain.data

/**
 * Data.
 * Created by cristi.paval on 3/26/18.
 */
data class UserSecurityData(
        val publicKeyIndex0: String,
        val publicKeyIndex188: String,
        val derivedPassword: ByteArray,
        val encryptedMasterKey: ByteArray,
        val masterKeyIV: ByteArray,
        val encryptedMnemonic: ByteArray,
        val mnemonicIV: ByteArray)