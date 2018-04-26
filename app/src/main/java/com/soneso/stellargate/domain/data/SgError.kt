package com.soneso.stellargate.domain.data

import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.SgNetworkError
import com.soneso.stellargate.model.dto.SgNetworkException
import com.soneso.stellargate.networking.NetworkUtil

class SgError(val errorResId: Int, message: String?) : Exception(message) {

    constructor(errorResId: Int) : this(errorResId, null)

    constructor(message: String?) : this(0, message)

    companion object
}

fun SgError.Companion.fromNetworkError(networkError: SgNetworkError): SgError {
    return when {
        networkError.isUnknownError() -> {
            SgError(R.string.unknown_error)
        }
        networkError.isNoInternetError() -> {
            SgError(R.string.no_internet_error)
        }
        else -> {
            val errorBuilder = StringBuilder()
            networkError.forEach {
                errorBuilder.append(it.message).append("\n")
            }
            SgError(errorBuilder.removeSuffix("\n").toString())
        }
    }
}

fun SgError.Companion.fromNetworkException(networkException: SgNetworkException): SgError {
    return when {
        networkException.validationErrors != null -> {
            val errorBuilder = StringBuilder()
            networkException.validationErrors.forEach {
                errorBuilder.append(it.message).append("\n")
            }
            SgError(errorBuilder.removeSuffix("\n").toString())
        }
        else -> {

            val errorResId = if (!NetworkUtil.isNetworkAvailable()) {
                R.string.no_internet_error
            } else {
                R.string.unknown_error
            }
            SgError(errorResId)
        }
    }
}