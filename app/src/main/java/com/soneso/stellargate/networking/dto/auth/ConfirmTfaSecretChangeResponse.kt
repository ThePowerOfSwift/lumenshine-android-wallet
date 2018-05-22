package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class ConfirmTfaSecretChangeResponse {

    @JsonProperty("mail_confirmed")
    var mailConfirmed = false

    @JsonProperty("tfa_confirmed")
    var tfaConfirmed = false

    @JsonProperty("mnemonic_confirmed")
    var mnemonicConfirmed = false
}