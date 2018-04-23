package com.soneso.stellargate.model.dto

import android.util.Log
import io.reactivex.observers.DisposableObserver
import okhttp3.Headers
import retrofit2.adapter.rxjava2.Result


abstract class ResponseObserver<T> : DisposableObserver<Result<T>>() {

    abstract fun onSuccess(headers: Headers, body: T?)

    abstract fun onException(e: Throwable?)

    override fun onNext(result: Result<T>) {
        if (result.isError) {
            onException(result.error())
        } else if (result.response() == null) {
            onException(NullPointerException("Null response!"))
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
        onException(e)
    }

    override fun onComplete() {
    }

    companion object {
        const val TAG = "ResponseObserver"
    }
}