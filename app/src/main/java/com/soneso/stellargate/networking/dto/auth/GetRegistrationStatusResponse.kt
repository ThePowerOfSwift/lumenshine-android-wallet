package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class GetRegistrationStatusResponse {

    @JsonProperty("tfa_enabled")
    var tfaConfirmed = false

    @JsonProperty("menmonic_confirmed")
    var mnemonicConfirmed = false

    @JsonProperty("mail_confirmed")
    var mailConfirmed = false
}