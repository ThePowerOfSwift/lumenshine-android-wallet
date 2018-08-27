package com.soneso.lumenshine.networking.api

import com.soneso.lumenshine.networking.dto.WalletDto
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

interface WalletApi {

    @GET("/portal/user/dashboard/get_user_wallets")
    fun getAllWallets(): Single<Response<List<WalletDto>>>
}