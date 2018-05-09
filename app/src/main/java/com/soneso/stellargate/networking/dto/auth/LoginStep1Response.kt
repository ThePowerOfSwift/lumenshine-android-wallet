package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.bouncycastle.util.encoders.Base64

class LoginStep1Response {

    @JsonProperty("kdf_password_salt")
    var passwordKdfSalt = ""

    @JsonProperty("encrypted_master_key")
    var encryptedMasterKey = ""

    @JsonProperty("master_key_encryption_iv")
    var masterKeyEncryptionIv = ""

    @JsonProperty("encrypted_mnemonic")
    var encryptedMnemonic = ""

    @JsonProperty("mnemonic_encryption_iv")
    var mnemonicEncryptionIv = ""

    @JsonProperty("public_key_index0")
    var publicKeyIndex0 = ""

    var jwtToken = ""

    fun passwordKdfSalt() = Base64.decode(passwordKdfSalt)

    fun encryptedMasterKey() = Base64.decode(encryptedMasterKey)

    fun masterKeyEncryptionIv() = Base64.decode(masterKeyEncryptionIv)

    fun encryptedMnemonic() = Base64.decode(encryptedMnemonic)

    fun mnemonicEncryptionIv() = Base64.decode(mnemonicEncryptionIv)
}