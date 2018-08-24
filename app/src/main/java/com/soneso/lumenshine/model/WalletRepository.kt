package com.soneso.lumenshine.model

import com.soneso.lumenshine.model.entities.StellarWallet
import com.soneso.lumenshine.model.entities.Wallet
import com.soneso.lumenshine.model.wrapper.toStellarWallet
import com.soneso.lumenshine.model.wrapper.toWallet
import com.soneso.lumenshine.networking.NetworkStateObserver
import com.soneso.lumenshine.networking.api.WalletApi
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.persistence.room.LdDatabase
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
        db: LdDatabase
) {

    private val walletApi = r.create(WalletApi::class.java)
    private val walletDao = db.walletDao()

    fun loadAllWallets(): Flowable<Resource<List<Wallet>, ServerException>> {

        return walletApi.getAllWallets()
                .asHttpResourceLoader(networkStateObserver)
                .mapResource({ dtoList ->
                    dtoList.map { dto ->
                        dto.toWallet()
                    }
                }, {
                    it
                })
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