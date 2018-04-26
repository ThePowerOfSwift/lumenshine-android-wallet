package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.soneso.stellargate.networking.dto.ValidationError

class RegistrationResponse {

    @JsonProperty("status")
    var status = ""

    @JsonProperty("validation_errors")
    var errors = emptyList<ValidationError>()

    @JsonProperty("tfa_secret")
    var token2fa = ""

    @JsonProperty("tfa_qr_image")
    var qrCode = ""
}