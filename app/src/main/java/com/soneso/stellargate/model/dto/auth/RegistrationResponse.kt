package com.soneso.stellargate.model.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class RegistrationResponse {

    @JsonProperty("status")
    var status = ""

    @JsonProperty("validation_errors")
    var errors = emptyList<ValidationError>()

    @JsonProperty("tfa_secret")
    var token2fa = ""

    @JsonProperty("tfa_qr_image")
    var qrCode = ""

    @JsonProperty("jwt_token")
    var jwtToken = ""
}