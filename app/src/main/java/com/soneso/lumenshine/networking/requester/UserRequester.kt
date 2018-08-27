package com.soneso.lumenshine.networking.requester

import com.soneso.lumenshine.networking.api.UserApi
import com.soneso.lumenshine.networking.dto.ResponseMapper
import com.soneso.lumenshine.networking.dto.auth.ChangePasswordRequest
import com.soneso.lumenshine.networking.dto.auth.GetUserAuthDataResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import javax.inject.Inject

class UserRequester
@Inject constructor(r: Retrofit) {

    private val userApi = r.create(UserApi::class.java)

    fun fetchUserSecurity(): Single<GetUserAuthDataResponse> {

        return userApi.getUserAuthData()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun changeUserPassword(request: ChangePasswordRequest): Single<Any> {

        return userApi
                .changePassword(
                        request.passwordKdfSalt,
                        request.encryptedMnemonicMasterKey,
                        request.mnemonicMasterKeyEncryptionIv,
                        request.encryptedWordListMasterKey,
                        request.wordListMasterKeyEncryptionIv,
                        request.publicKeyIndex188
                )
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }
}