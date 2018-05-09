package com.soneso.stellargate.model.account

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.soneso.stellargate.domain.data.StellarAccount
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
class AccountSyncer() : com.soneso.stellargate.model.account.AccountRepository {

    override fun createUserAccount(accountId: String) {

        Observable
                .fromCallable {
                    val friendbotUrl = String.format(
                            "https://horizon-testnet.stellar.org/friendbot?addr=%s",
                            accountId)
                    val response = URL(friendbotUrl).openStream()
                    val body = Scanner(response, "UTF-8").useDelimiter("\\A").next()
                    Log.d(com.soneso.stellargate.model.account.AccountSyncer.TAG, "SUCCESS! You have a new account :)\n$body")
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun getAccountDetails(liveData: MutableLiveData<StellarAccount>) {
        val accountId = "ahsdka7asd6f7asdrfasd This is a fake id"

        Observable
                .fromCallable {
                    val server = Server("https://horizon-testnet.stellar.org")
                    val account = server.accounts().account(KeyPair.fromAccountId(accountId))
                    Log.d(com.soneso.stellargate.model.account.AccountSyncer.TAG, "Account details retrieved successfully!")
                    return@fromCallable account
                }
                .subscribeOn(Schedulers.newThread())
                .map { account: AccountResponse ->
                    return@map StellarAccount(accountId, account.balances[0].balance)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveData.value = it
                }
    }

    companion object {
        const val TAG = "AccountSyncer"
    }
}