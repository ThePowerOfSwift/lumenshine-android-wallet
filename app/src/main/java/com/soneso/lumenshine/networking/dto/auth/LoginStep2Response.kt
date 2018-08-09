package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class LoginStep2Response {

    var jwtToken = ""

    @JsonProperty("mail_confirmed")
    var emailConfirmed = false

    @JsonProperty("mnemonic_confirmed")
    var mnemonicConfirmed = false

    @JsonProperty("tfa_secret")
    var tfaSecret = ""

    @JsonProperty("tfa_qr_image")
    var tfaImageData = ""

    @JsonProperty("tfa_confirmed")
    var tfaConfirmed = false
}