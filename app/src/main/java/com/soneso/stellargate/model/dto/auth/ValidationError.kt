package com.soneso.stellargate.model.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationError {

    @JsonProperty("error_code")
    var code = 0

    @JsonProperty("invalid_argument")
    var invalidAttr = ""

    @JsonProperty("message")
    var message = ""
}