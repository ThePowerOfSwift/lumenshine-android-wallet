package com.soneso.lumenshine.domain.data

import com.soneso.lumenshine.R
import com.soneso.lumenshine.networking.NetworkUtil
import com.soneso.lumenshine.networking.dto.SgNetworkException
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function

class SgError(val errorResId: Int, message: String?, val errorCode: Int = ErrorCodes.UNKNOWN) : Exception(message) {

    constructor(errorResId: Int) : this(errorResId, null)

    constructor(message: String?) : this(0, message)

    constructor(errorResId: Int, errorCode: Int) : this(errorResId, null, errorCode)

    constructor(message: String?, errorCode: Int) : this(0, message, errorCode)

    companion object
}

fun <T> SgError.Companion.singleFromNetworkException(): Function<Throwable, SingleSource<T>> {

    return Function {
        val networkException = it as SgNetworkException
        when {
            networkException.validationErrors != null -> {
                val errorBuilder = StringBuilder()
                if (networkException.validationErrors.size == 1) {
                    val validationError = networkException.validationErrors[0]
                    Single.error(SgError(validationError.message, validationError.code))
                } else {
                    networkException.validationErrors.forEach {
                        errorBuilder.append(it.message).append("\n")
                    }
                    Single.error(SgError(errorBuilder.removeSuffix("\n").toString()))
                }
            }
            else -> {

                val errorResId = if (!NetworkUtil.isNetworkAvailable()) {
                    R.string.no_internet_error
                } else {
                    R.string.unknown_error
                }
                Single.error(SgError(errorResId))
            }
        }
    }
}