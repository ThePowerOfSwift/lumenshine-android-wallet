package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class RegistrationResponse {

    @JsonProperty("tfa_secret")
    var token2fa = ""

    @JsonProperty("tfa_qr_image")
    var tfaImageData = ""

    var jwtToken = ""
}