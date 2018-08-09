package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class GetTfaRequestResponse {

    @JsonProperty("tfa_secret")
    var tfaSecret = ""
}