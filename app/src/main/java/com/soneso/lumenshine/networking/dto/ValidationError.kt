package com.soneso.lumenshine.networking.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationError {

    @JsonProperty("error_code")
    var code = 0

    @JsonProperty("parameter_name")
    var invalidAttr = ""

    @JsonProperty("user_error_message_key")
    var msgResName = ""
        get() = msgResName.replace(".", "_")

    @JsonProperty("error_message")
    var message = ""
}