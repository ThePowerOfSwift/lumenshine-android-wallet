package com.soneso.stellargate.model.dto

import io.reactivex.observers.DisposableObserver


abstract class ResponseObserver<T> : DisposableObserver<T>() {

    abstract fun onResponse(data: T)

    abstract fun onException(e: Throwable)

    override fun onNext(t: T) {
        onResponse(t)
    }

    override fun onError(e: Throwable) {
        onException(e)
    }

    override fun onComplete() {
    }
}