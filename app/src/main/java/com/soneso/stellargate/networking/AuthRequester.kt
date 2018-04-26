package com.soneso.stellargate.networking

import com.soneso.stellargate.domain.DataMapper
import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.model.dto.ResponseObserver
import com.soneso.stellargate.model.dto.SgNetworkError
import com.soneso.stellargate.model.dto.auth.GetSalutationListResponse
import com.soneso.stellargate.model.dto.auth.RegistrationResponse
import com.soneso.stellargate.model.dto.auth.TfaRegistrationResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Headers
import org.bouncycastle.util.encoders.Base64

class AuthRequester(private val authApi: AuthApi, private val sessionProfile: SessionProfileService) {

    fun registerUser(account: Account, responseObserver: ResponseObserver<RegistrationResponse>) {
        authApi.registerUser(
                account.email,
                Base64.toBase64String(account.passwordSalt),
                Base64.toBase64String(account.encryptedMasterKey),
                Base64.toBase64String(account.masterKeyIV),
                Base64.toBase64String(account.encryptedMnemonic),
                Base64.toBase64String(account.mnemonicIV),
                account.publicKeyIndex0,
                account.publicKeyIndex188
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : ResponseObserver<RegistrationResponse>() {
                    override fun onSuccess(headers: Headers, body: RegistrationResponse?) {
                        val authHeader = headers[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            sessionProfile.authToken = authHeader
                        }
                        responseObserver.onSuccess(headers, body)
                    }

                    override fun onError(error: SgNetworkError) {
                        responseObserver.onError(error)
                    }
                })
    }

    fun confirmTfaRegistration(tfaCode: String, responseObserver: ResponseObserver<TfaRegistrationResponse>) {
        authApi.confirmTfaRegistration(sessionProfile.authToken, tfaCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(responseObserver)
    }

    fun fetchSalutationList(): Single<GetSalutationListResponse> {
        return authApi.getSalutationList(sessionProfile.langKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(DataMapper())
    }
}