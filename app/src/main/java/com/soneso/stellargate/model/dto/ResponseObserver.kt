package com.soneso.stellargate.model.dto

import io.reactivex.observers.DisposableObserver


class ResponseObserver<T>(private val dataProvider: DataProvider<T>) : DisposableObserver<T>() {

    override fun onNext(t: T) {
        dataProvider.data = t
        dataProvider.status = DataStatus.SUCCESS
    }

    override fun onError(e: Throwable) {
        dataProvider.status = DataStatus.ERROR
    }

    override fun onComplete() {
    }
}