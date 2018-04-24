package com.soneso.stellargate.model.dto

import android.util.Log
import com.soneso.stellargate.networking.NetworkUtil
import io.reactivex.observers.DisposableObserver
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.Result


abstract class ResponseObserver<T> : DisposableObserver<Result<T>>() {

    abstract fun onSuccess(headers: Headers, body: T?)

    abstract fun onError(error: SgNetworkError)

    override fun onNext(result: Result<T>) {
        if (result.isError || result.response() == null) {
            handleException(result.error())
        } else {
            val response = result.response()!!
            if (response.isSuccessful) {
                onSuccess(response.headers(), response.body())
            } else {
                handleErrorResponse(response.errorBody())
            }
        }
    }

    private fun handleErrorResponse(errorBody: ResponseBody?) {
        if (errorBody == null) {
            handleException(null)
            return
        }
        val networkError = OBJECT_MAPPER.readValue(errorBody.string(), SgNetworkError::class.java)
        onError(networkError)
    }

    override fun onError(e: Throwable) {
        handleException(e)
    }

    private fun handleException(e: Throwable?) {
        if (e != null) {
            Log.e(TAG, "Exception", e)
        }
        val networkError = when {
            !NetworkUtil.isNetworkAvailable() -> {
                SgNetworkError(LocalErrorType.NO_INTERNET)
            }
            else -> {
                SgNetworkError(LocalErrorType.UNKNOWN)
            }
        }

        onError(networkError)
    }

    override fun onComplete() {
    }

    companion object {
        const val TAG = "ResponseObserver"
        val OBJECT_MAPPER = Parse.createMapper()
    }
}