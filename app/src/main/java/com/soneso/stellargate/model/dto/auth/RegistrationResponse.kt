package com.soneso.stellargate.model.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class RegistrationResponse {

    @JsonProperty("status")
    var status = ""

    @JsonProperty("validation_errors")
    var errors = emptyList<ValidationError>()

    @JsonProperty("2FA_secret")
    var token2fa = ""

    @JsonProperty("2FA_qr_image")
    var qrCode = ""
}