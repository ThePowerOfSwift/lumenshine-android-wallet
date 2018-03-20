package com.soneso.stellargate.networking

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.soneso.stellargate.model.StellarAccount
import com.soneso.stellargate.persistence.SgPrefs
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.responses.AccountResponse
import java.net.URL
import java.util.*


/**
 * Class used to perform requests.
 * Created by cristi.paval on 3/9/18.
 */
class RequestManager(private val sgPrefs: SgPrefs) {

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

    fun getAccountDetails(liveData: MutableLiveData<StellarAccount>) {
        val accountId = sgPrefs.accountId()

        Observable
                .fromCallable {
                    val server = Server("https://horizon-testnet.stellar.org")
                    val account = server.accounts().account(KeyPair.fromAccountId(accountId))
                    return@fromCallable account
                }
                .subscribeOn(Schedulers.newThread())
                .map { account: AccountResponse ->
                    val sa = StellarAccount()
                    sa.accountId = accountId
                    sa.balance = account.balances[0].balance
                    return@map sa
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveData.value = it
                }
    }

    companion object {
        const val TAG = "RequestManager"
    }
}