package com.soneso.stellargate.domain

import java.util.*

/**
 * Data.
 * Created by cristi.paval on 3/26/18.
 */
data class UserSecurityData(
        val publicKeyIndex0: ByteArray,
        val publicKeyIndex188: ByteArray,
        val derivedPassword: ByteArray,
        val encryptedMasterKey: ByteArray,
        val masterKeyIV: ByteArray,
        val encryptedMnemonic: ByteArray,
        val mnemonicIV: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSecurityData

        if (!Arrays.equals(publicKeyIndex0, other.publicKeyIndex0)) return false
        if (!Arrays.equals(publicKeyIndex188, other.publicKeyIndex188)) return false
        if (!Arrays.equals(derivedPassword, other.derivedPassword)) return false
        if (!Arrays.equals(encryptedMasterKey, other.encryptedMasterKey)) return false
        if (!Arrays.equals(masterKeyIV, other.masterKeyIV)) return false
        if (!Arrays.equals(encryptedMnemonic, other.encryptedMnemonic)) return false
        if (!Arrays.equals(mnemonicIV, other.mnemonicIV)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(publicKeyIndex0)
        result = 31 * result + Arrays.hashCode(publicKeyIndex188)
        result = 31 * result + Arrays.hashCode(derivedPassword)
        result = 31 * result + Arrays.hashCode(encryptedMasterKey)
        result = 31 * result + Arrays.hashCode(masterKeyIV)
        result = 31 * result + Arrays.hashCode(encryptedMnemonic)
        result = 31 * result + Arrays.hashCode(mnemonicIV)
        return result
    }
}