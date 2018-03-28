package com.soneso.stellargate.model.dto

/**
 * Dto.
 * Created by cristi.paval on 3/26/18.
 */
data class RegistrationRequest(
        /*@JsonProperty("email")              */  val email: String,
        /*@JsonProperty("publicKey")          */  val publicKeyIndex0: String,
        /*@JsonProperty("publicKey188")       */  val publicKeyIndex188: String,
        /*@JsonProperty("passwordSalt")       */  val derivedPassword: ByteArray,
        /*@JsonProperty("encryptedMasterKey") */  val encryptedMasterKey: ByteArray,
        /*@JsonProperty("masterKeyIV")        */  val masterKeyIV: ByteArray,
        /*@JsonProperty("encryptedMnemonic")  */  val encryptedMnemonic: ByteArray,
        /*@JsonProperty("mnemonicIV")         */  val mnemonicIV: ByteArray
)