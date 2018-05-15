package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class GetTfaRequestResponse {

    @JsonProperty("tfa_secret")
    var tfaSecret = ""
}