package com.soneso.stellargate.networking.requester

import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.networking.SessionProfileService
import com.soneso.stellargate.networking.api.AuthApi
import com.soneso.stellargate.networking.api.SgApi
import com.soneso.stellargate.networking.dto.ResponseMapper
import com.soneso.stellargate.networking.dto.ResultMapper
import com.soneso.stellargate.networking.dto.auth.GetCountryListResponse
import com.soneso.stellargate.networking.dto.auth.GetSalutationListResponse
import com.soneso.stellargate.networking.dto.auth.RegistrationResponse
import com.soneso.stellargate.networking.dto.auth.TfaRegistrationResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.bouncycastle.util.encoders.Base64
import retrofit2.Response

class AuthRequester(private val authApi: AuthApi, private val sessionProfile: SessionProfileService) {

    fun registerUser(account: Account): Single<RegistrationResponse> {
        val mapper = ResponseMapper<RegistrationResponse>()
        return authApi.registerUser(
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
                .map(object : ResultMapper<RegistrationResponse>() {
                    override fun handleSuccess(response: Response<RegistrationResponse>): RegistrationResponse {
                        val authHeader = response.headers()[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            sessionProfile.authToken = authHeader
                        }
                        return mapper.handleSuccess(response)
                    }

                    override fun handleError(errorBody: ResponseBody?): Throwable {
                        return mapper.handleError(errorBody)
                    }
                })
    }

    fun confirmTfaRegistration(tfaCode: String): Single<TfaRegistrationResponse> {
        return authApi.confirmTfaRegistration(sessionProfile.authToken, tfaCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ResponseMapper())
    }

    fun fetchSalutationList(): Single<GetSalutationListResponse> {
        return authApi.getSalutationList(sessionProfile.langKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ResponseMapper())
    }

    fun fetchCountryList(): Single<GetCountryListResponse> {
        return authApi.getCountryList(sessionProfile.langKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ResponseMapper())
    }
}