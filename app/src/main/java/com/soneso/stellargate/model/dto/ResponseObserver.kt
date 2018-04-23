package com.soneso.stellargate.model.dto

import android.util.Log
import com.soneso.stellargate.networking.NetworkUtil
import io.reactivex.observers.DisposableObserver
import okhttp3.Headers
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
                Log.e(TAG, "Error!" + response.message())
            }
        }
    }

    override fun onError(e: Throwable) {
        handleException(e)
    }

    private fun handleException(e: Throwable?) {
        if (e != null) {
            Log.e(TAG, "Exception", e)
        }
        val code = when {
            !NetworkUtil.isNetworkAvailable() -> {
                SgErrorStatus.NO_INTERNET
            }
            else -> {
                SgErrorStatus.UNKNOWN
            }
        }
        val networkError = SgNetworkError()
        val errorStatus = SgErrorStatus()
        errorStatus.code = code
        networkError.errorStatus = errorStatus

        onError(networkError)
    }

    override fun onComplete() {
    }

    companion object {
        const val TAG = "ResponseObserver"
    }
}