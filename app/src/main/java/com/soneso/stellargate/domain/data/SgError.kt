package com.soneso.stellargate.domain.data

import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.SgNetworkError

class SgError {

    var errorResId = 0
    var message: CharSequence = ""

    companion object
}

fun SgError.Companion.fromNetworkError(networkError: SgNetworkError): SgError {
    val error = SgError()
    when {
        networkError.isUnknownError() -> {
            error.errorResId = R.string.unknown_error
        }
        networkError.isNoInternetError() -> {
            error.errorResId = R.string.no_internet_error
        }
        else -> {
            val errorBuilder = StringBuilder()
            networkError.forEach {
                errorBuilder.append(it.message).append("\n")
            }
            error.message = errorBuilder.removeSuffix("\n")
        }
    }
    return error
}