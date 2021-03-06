package com.soneso.lumenshine.model

import com.soneso.lumenshine.model.entities.StellarWallet
import com.soneso.lumenshine.model.entities.Wallet
import com.soneso.lumenshine.model.wrapper.toStellarWallet
import com.soneso.lumenshine.model.wrapper.toWallet
import com.soneso.lumenshine.networking.NetworkStateObserver
import com.soneso.lumenshine.networking.api.WalletApi
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.persistence.room.LsDatabase
import com.soneso.lumenshine.util.*
import io.reactivex.Flowable
import io.reactivex.Single
import org.stellar.sdk.Server
import org.stellar.sdk.responses.AccountResponse
import retrofit2.Retrofit
import javax.inject.Inject

class WalletRepository @Inject constructor(
        private val networkStateObserver: NetworkStateObserver,
        r: Retrofit,
        private val stellarServer: Server,
        db: LsDatabase
) {

    private val walletApi = r.create(WalletApi::class.java)
    private val walletDao = db.walletDao()

    fun loadAllWallets(): Flowable<Resource<List<Wallet>, ServerException>> {

        val refresher: Flowable<Resource<List<Wallet>, ServerException>> = walletApi.getAllWallets()
                .asHttpResourceLoader(networkStateObserver)
                .mapResource({ dto ->
                    dto.map { it.toWallet() }
                }, { it })

        return walletDao.getAllWallets()
                .map { Success<List<Wallet>, ServerException>(it) as Resource<List<Wallet>, ServerException> }
                .refreshWith(refresher) { walletDao.insertAll(it) }
    }

    fun loadStellarWallet(mnemonic: CharArray): Single<Resource<StellarWallet, ServerException>> {


        return Single.create<Resource<AccountResponse, ServerException>> {
            try {
                val ar = stellarServer.accounts().account(KeyPairHelper.keyPair(mnemonic))
                it.onSuccess(Success(ar))
            } catch (e: Exception) {
                it.onSuccess(Failure(ServerException(e)))
            }
        }
                .mapResource({ it.toStellarWallet() }, { it })
    }
}