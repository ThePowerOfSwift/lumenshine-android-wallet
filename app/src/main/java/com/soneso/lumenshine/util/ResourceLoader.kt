package com.soneso.lumenshine.util

import android.util.Log
import com.soneso.lumenshine.networking.NetworkStateObserver
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "ResourceLoader"

fun <ResponseType> Single<Response<ResponseType>>.asHttpResourceLoader(networkStateObserver: NetworkStateObserver, retryCount: Int = 3): Flowable<Resource<ResponseType, ServerException>> {

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

    return Flowable.create<Resource<ResponseType, ServerException>>({ emitter ->

        emitter.onNext(Resource(Resource.LOADING))
        val disposable = this.retryWhen(retryMechanism)
                .subscribe({
                    if (it.isSuccessful) {
                        Log.d(TAG, "_______Response is success!")
                        emitter.onNext(Success(it.body()!!))
                    } else {
                        Log.d(TAG, "_______Response is errorBody!")
                        emitter.onNext(Failure(ServerException(it.errorBody())))
                    }
                }, {
                    emitter.onNext(Failure(ServerException(it)))
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

fun <SuccessType, FailureType> Flowable<SuccessType>.wrapInResource(): Flowable<Resource<SuccessType, FailureType>> {

    return this.map { Success<SuccessType, FailureType>(it) }
}

fun <SuccessType, FailureType> Flowable<Resource<SuccessType, FailureType>>.refreshWith(
        refresher: Flowable<Resource<SuccessType, FailureType>>,
        cacheFunction: ((SuccessType) -> Unit)? = null
): Flowable<Resource<SuccessType, FailureType>> {

    return Flowable.create({ emitter ->

        var state = Resource.LOADING

        val thisD = this.subscribe {
            Log.d(TAG, "Emitting result from initial source...")
            val resource = if (it.state == Resource.SUCCESS) {
                Resource(state, it.success())
            } else {
                it
            }
            emitter.onNext(resource)
        }
        val refresherD = refresher
                .subscribe({
                    Log.d(TAG, "Emitting result from refresher...")
                    state = it.state
                    if (it.state == Resource.SUCCESS && cacheFunction != null) {
                        cacheFunction.invoke(it.success())
                    } else {
                        emitter.onNext(it)
                    }
                }, {
                    Log.d(TAG, "Emitting error...")
                    emitter.onError(it)
                })

        emitter.setCancellable {
            Log.d(TAG, "Disposing...")
            thisD.dispose()
            refresherD.dispose()
        }
    }, BackpressureStrategy.LATEST)
}