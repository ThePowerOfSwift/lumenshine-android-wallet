package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginStep2Response(

        @JsonProperty("mail_confirmed")
        var emailConfirmed: Boolean = false,

        @JsonProperty("mnemonic_confirmed")
        var mnemonicConfirmed: Boolean = false,

        @JsonProperty("tfa_secret")
        var tfaSecret: String = "",

        @JsonProperty("tfa_confirmed")
        var tfaConfirmed: Boolean = false
)