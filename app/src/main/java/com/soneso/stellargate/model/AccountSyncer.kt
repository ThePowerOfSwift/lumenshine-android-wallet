package com.soneso.stellargate.model

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.soneso.stellargate.domain.StellarAccount
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
class AccountSyncer(private val sgPrefs: SgPrefs) : AccountRepository {

    override fun createAccount(accountId: String) {

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
                    Log.d(TAG, "Account details retrieved successfully!")
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