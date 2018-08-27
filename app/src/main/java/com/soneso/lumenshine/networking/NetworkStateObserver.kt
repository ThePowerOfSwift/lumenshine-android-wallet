package com.soneso.lumenshine.networking

import android.content.Context
import android.net.NetworkInfo
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject


class NetworkStateObserver @Inject constructor(private val context: Context) {

    fun observeApiAccess(): Flowable<Boolean> {

        return ReactiveNetwork
                .observeNetworkConnectivity(context)
                .map {
                    val state = it.state() == NetworkInfo.State.CONNECTED
                    Log.d(TAG, "Network connected: $state")
                    state
                }
                .toFlowable(BackpressureStrategy.LATEST)
    }

    companion object {

        const val TAG = "NetworkStateObserver"
    }
}