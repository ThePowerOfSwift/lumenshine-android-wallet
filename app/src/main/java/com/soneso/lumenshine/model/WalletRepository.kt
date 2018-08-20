package com.soneso.lumenshine.model

import com.soneso.lumenshine.domain.data.wallet.Wallet
import com.soneso.lumenshine.domain.data.wallet.toWallet
import com.soneso.lumenshine.networking.NetworkStateObserver
import com.soneso.lumenshine.networking.api.WalletApi
import com.soneso.lumenshine.networking.asHttpResourceLoader
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.util.Resource
import com.soneso.lumenshine.util.mapResource
import io.reactivex.Flowable
import retrofit2.Retrofit
import javax.inject.Inject

class WalletRepository @Inject constructor(
        private val networkStateObserver: NetworkStateObserver,
        r: Retrofit
) {

    private val walletApi = r.create(WalletApi::class.java)

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
}