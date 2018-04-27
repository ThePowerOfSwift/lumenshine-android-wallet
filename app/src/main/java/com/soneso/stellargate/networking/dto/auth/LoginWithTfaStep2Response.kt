package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class LoginWithTfaStep2Response {

    @JsonProperty("tfa_secret")
    var tfaSecret = ""

    @JsonProperty("email_confirmed")
    var emailConfirmed = false

    @JsonProperty("mnemonic_confirmed")
    var mnemonicConfirmed = false
}