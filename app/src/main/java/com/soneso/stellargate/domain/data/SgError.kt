package com.soneso.stellargate.domain.data

import com.soneso.stellargate.R
import com.soneso.stellargate.networking.NetworkUtil
import com.soneso.stellargate.networking.dto.SgNetworkException
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function

class SgError(val errorResId: Int, message: String?) : Exception(message) {

    constructor(errorResId: Int) : this(errorResId, null)

    constructor(message: String?) : this(0, message)

    companion object
}

fun <T> SgError.Companion.singleFromNetworkException(): Function<Throwable, SingleSource<T>> {

    return Function {
        val networkException = it as SgNetworkException
        when {
            networkException.validationErrors != null -> {
                val errorBuilder = StringBuilder()
                networkException.validationErrors.forEach {
                    errorBuilder.append(it.message).append("\n")
                }
                Single.error(SgError(errorBuilder.removeSuffix("\n").toString()))
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