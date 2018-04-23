package com.soneso.stellargate.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.soneso.stellargate.model.dto.auth.ValidationError

class SgNetworkError {

    @JsonProperty("error_status")
    var errorStatus: SgErrorStatus = SgErrorStatus()

    @JsonProperty("errors")
    val validationErrors: List<ValidationError>? = null
}

class SgErrorStatus {

    @JsonProperty("code")
    var code = UNKNOWN

    @JsonProperty("message")
    var message = ""

    fun isNoInternetError() = code == NO_INTERNET

    fun isUnknownError() = code == UNKNOWN

    companion object {
        const val UNKNOWN = -1
        const val NO_INTERNET = -2
    }
}