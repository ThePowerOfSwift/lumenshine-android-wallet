package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class ConfirmTfaResponse {

    var jwtToken = ""

    @JsonProperty("mail_confirmed")
    var emailConfirmed = false

    @JsonProperty("mnemonic_confirmed")
    var mnemonicConfirmed = false

    @JsonProperty("tfa_confirmed")
    var tfaConfirmed = false
}