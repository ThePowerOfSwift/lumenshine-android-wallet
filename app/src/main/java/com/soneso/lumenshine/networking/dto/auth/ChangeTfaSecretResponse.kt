package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class ChangeTfaSecretResponse {

    @JsonProperty("tfa_secret")
    var tfaSecret = ""

    @JsonProperty("tfa_qr_image")
    var tfaImageData = ""
}