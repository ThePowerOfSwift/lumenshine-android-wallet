package com.soneso.stellargate.networking.requester

import com.soneso.stellargate.networking.SessionProfileService
import com.soneso.stellargate.networking.api.AuthApi
import com.soneso.stellargate.networking.api.SgApi
import com.soneso.stellargate.networking.dto.ResponseMapper
import com.soneso.stellargate.networking.dto.ResultMapper
import com.soneso.stellargate.networking.dto.auth.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRequester(private val authApi: AuthApi, private val sessionProfile: SessionProfileService) {

    fun registerUser(request: RegistrationRequest): Single<RegistrationResponse> {

        val mapper = ResponseMapper<RegistrationResponse>()
        return authApi.registerUser(
                request.email,
                request.passwordKdfSalt,
                request.encryptedMasterKey,
                request.masterKeyEncryptionIv,
                request.encryptedMnemonic,
                request.mnemonicEncryptionIv,
                request.publicKeyIndex0,
                request.publicKeyIndex188,
                request.countryCode
        )
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<RegistrationResponse>() {

                    override fun handleSuccess(response: Response<RegistrationResponse>): RegistrationResponse {

                        val registrationResponse = mapper.handleSuccess(response)

                        val authHeader = response.headers()[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            sessionProfile.authToken = authHeader
                            registrationResponse.jwtToken = authHeader
                        }
                        return registrationResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun confirmTfaRegistration(tfaCode: String): Single<TfaRegistrationResponse> {

        return authApi.confirmTfaRegistration(sessionProfile.authToken, tfaCode)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchSalutationList(): Single<GetSalutationListResponse> {

        return authApi.getSalutationList(sessionProfile.langKey)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchCountryList(): Single<GetCountryListResponse> {

        return authApi.getCountryList(sessionProfile.langKey)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun loginWithTfaStep1(request: LoginWithTfaStep1Request): Single<LoginWithTfaStep1Response> {

        val mapper = ResponseMapper<LoginWithTfaStep1Response>()
        return authApi.loginWithTfaStep1(request.email, request.tfaCode)
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<LoginWithTfaStep1Response>() {

                    override fun handleSuccess(response: Response<LoginWithTfaStep1Response>): LoginWithTfaStep1Response {

                        val loginResponse = mapper.handleSuccess(response)

                        val authHeader = response.headers()[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            sessionProfile.authToken = authHeader
                            loginResponse.jwtToken = authHeader
                        }
                        return loginResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun loginWithTfaStep2(request: LoginWithTfaStep2Request): Single<LoginWithTfaStep2Response> {

        return authApi.loginWithTfaStep2(sessionProfile.authToken, request.publicKeyIndex188)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun confirmMnemonic(): Single<Unit> {
        return authApi.confirmMnemonic(sessionProfile.authToken)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
                .observeOn(AndroidSchedulers.mainThread())
    }
}