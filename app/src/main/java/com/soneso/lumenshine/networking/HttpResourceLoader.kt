package com.soneso.lumenshine.networking

import android.util.Log
import com.soneso.lumenshine.util.Resource
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "HttpResourceLoader"

fun <ResponseType> Single<Response<ResponseType>>.asHttpResourceLoader(networkStateObserver: NetworkStateObserver, retryCount: Int = 3): Flowable<ApiResource<ResponseType>> {

    val retryMechanism = Function<Flowable<Throwable>, Flowable<Boolean>> { networkThrowable ->
        val counter = AtomicInteger()
        networkThrowable
                .takeWhile { counter.getAndIncrement() < retryCount }
                .doOnNext { throwable ->
                    Log.d(TAG, "Trying number: ${counter.get()}")
                    if (counter.get() == 1) {
                        Log.e(TAG, "--- Encountered throwable", throwable)
                    }
                    if (!throwable.isWorthRetry()) {
                        throw throwable
                    }
                }
                .flatMap { networkStateObserver.observeApiAccess() }
                .doOnNext { Log.d(TAG, "--- Api accessible: $it") }
                .filter { it }
    }

    return Flowable.create<ApiResource<ResponseType>>({ emitter ->

        emitter.onNext(ApiResource(Resource.LOADING))
        val disposable = this.retryWhen(retryMechanism)
                .subscribe({
                    if (it.isSuccessful) {
                        Log.d(TAG, "_______Response is success!")
                        emitter.onNext(ApiSuccess(it.body()!!))
                    } else {
                        Log.d(TAG, "_______Response is errorBody!")
                        emitter.onNext(ApiFailure(ServerException(it.errorBody())))
                    }
                }, {
                    emitter.onNext(ApiFailure(ServerException(it)))
                })

        emitter.setCancellable {
            disposable.dispose()
        }
    }, BackpressureStrategy.LATEST)
}

private fun Throwable.isWorthRetry(): Boolean {

    return when (this) {
        is IOException -> true
        else -> false
    }
}