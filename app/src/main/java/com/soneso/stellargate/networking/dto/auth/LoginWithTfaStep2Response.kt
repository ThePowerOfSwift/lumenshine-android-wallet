package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class LoginWithTfaStep2Response {

    @JsonProperty("tfa_secret")
    var tfaSecret = ""

    @JsonProperty("mail_confirmed")
    var emailConfirmed = false

    @JsonProperty("menmonic_confirmed")
    var mnemonicConfirmed = false

    @JsonProperty("tfa_enabled")
    var tfaConfirmed = false
}