package com.soneso.stellargate.domain.data

import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.SgErrorStatus
import com.soneso.stellargate.model.dto.SgNetworkError

class SgError {

    var errorResId = 0
    var message = ""

    companion object {

    }
}

fun SgError.Companion.fromNetworkError(networkError: SgNetworkError): SgError {
    val errorStatus = networkError.errorStatus
    val error = SgError()

    when (errorStatus.code) {
        SgErrorStatus.UNKNOWN -> {
            error.errorResId = R.string.unknown_error
        }
        SgErrorStatus.NO_INTERNET -> {
            error.errorResId = R.string.no_internet_error
        }
        else -> {
            error.message = errorStatus.message
        }
    }
    return error
}