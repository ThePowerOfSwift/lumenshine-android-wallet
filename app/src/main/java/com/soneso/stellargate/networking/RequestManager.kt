package com.soneso.stellargate.networking

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL
import java.util.*

/**
 * Class used to perform requests.
 * Created by cristi.paval on 3/9/18.
 */
class RequestManager {

    fun createAccount(accountId: String) {

        Observable
                .fromCallable {
                    val friendbotUrl = String.format(
                            "https://horizon-testnet.stellar.org/friendbot?addr=%s",
                            accountId)
                    val response = URL(friendbotUrl).openStream()
                    val body = Scanner(response, "UTF-8").useDelimiter("\\A").next()
                    Log.d(TAG, "SUCCESS! You have a new account :)\n$body")
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    companion object {
        const val TAG = "RequestManager"
    }
}