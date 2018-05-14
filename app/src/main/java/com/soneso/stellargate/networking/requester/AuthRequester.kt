package com.soneso.stellargate.networking.requester

import com.soneso.stellargate.networking.api.AuthApi
import com.soneso.stellargate.networking.api.SgApi
import com.soneso.stellargate.networking.dto.ResponseMapper
import com.soneso.stellargate.networking.dto.ResultMapper
import com.soneso.stellargate.networking.dto.auth.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRequester(private val authApi: AuthApi) {

    fun registerUser(request: RegistrationRequest): Single<RegistrationResponse> {

        val mapper = ResponseMapper<RegistrationResponse>()
        return authApi.registerUser(
                request.email,
                request.passwordKdfSalt,
                request.encryptedMnemonicMasterKey,
                request.mnemonicMasterKeyEncryptionIv,
                request.encryptedMnemonic,
                request.mnemonicEncryptionIv,
                request.encryptedWordListMasterKey,
                request.wordListMasterKeyEncryptionIv,
                request.encryptedWordList,
                request.wordListEncryptionIv,
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
                            registrationResponse.jwtToken = authHeader
                        }
                        return registrationResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
    }

    fun confirmTfaRegistration(tfaCode: String): Single<ConfirmTfaResponse> {

        val mapper = ResponseMapper<ConfirmTfaResponse>()
        return authApi.confirmTfaRegistration(tfaCode)
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<ConfirmTfaResponse>() {
                    override fun handleSuccess(response: Response<ConfirmTfaResponse>): ConfirmTfaResponse {

                        val registrationResponse = mapper.handleSuccess(response)
                        val authHeader = response.headers()[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            registrationResponse.jwtToken = authHeader
                        }
                        return registrationResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
    }

    fun fetchSalutationList(): Single<GetSalutationListResponse> {

        return authApi.getSalutationList()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchCountryList(): Single<GetCountryListResponse> {

        return authApi.getCountryList()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun loginStep1(request: LoginStep1Request): Single<LoginStep1Response> {

        val mapper = ResponseMapper<LoginStep1Response>()
        return authApi.loginStep1(request.email, request.tfaCode)
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<LoginStep1Response>() {

                    override fun handleSuccess(response: Response<LoginStep1Response>): LoginStep1Response {

                        val loginResponse = mapper.handleSuccess(response)

                        val authHeader = response.headers()[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            loginResponse.jwtToken = authHeader
                        }
                        return loginResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
    }

    fun loginStep2(request: LoginStep2Request): Single<LoginStep2Response> {

        val mapper = ResponseMapper<LoginStep2Response>()
        return authApi.loginStep2(request.publicKeyIndex188)
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<LoginStep2Response>() {

                    override fun handleSuccess(response: Response<LoginStep2Response>): LoginStep2Response {

                        val loginResponse = mapper.handleSuccess(response)

                        val authHeader = response.headers()[SgApi.HEADER_NAME_AUTHORIZATION]
                        if (authHeader != null) {
                            loginResponse.jwtToken = authHeader
                        }
                        return loginResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
    }

    fun confirmMnemonic(): Single<Any> {
        return authApi.confirmMnemonic()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun resendConfirmationMail(request: ResendConfirmationMailRequest): Single<Any> {
        return authApi.resendConfirmationMail(request.email)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchRegistrationStatus(): Single<GetRegistrationStatusResponse> {
        return authApi.getRegistrationStatus()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }
}