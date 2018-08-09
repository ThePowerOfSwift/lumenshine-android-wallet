package com.soneso.lumenshine.networking.dto

import io.reactivex.functions.Function
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result

abstract class ResultMapper<T> : Function<Result<T>, T> {

    override fun apply(result: Result<T>): T {

        if (result.isError || result.response() == null) {
            throw SgNetworkException(result.error())

        } else {
            val response = result.response()!!
            if (response.isSuccessful) {
                return handleSuccess(response)

            } else {
                throw handleError(response.errorBody())
            }
        }
    }

    abstract fun handleSuccess(response: Response<T>): T

    abstract fun handleError(errorBody: ResponseBody?): Throwable
}