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
                            registrationResponse.jwtToken = authHeader
                        }
                        return registrationResponse
                    }

                    override fun handleError(errorBody: ResponseBody?) = mapper.handleError(errorBody)
                })
    }

    fun confirmTfaRegistration(jwtToken: String, tfaCode: String): Single<ConfirmTfaResponse> {

        val mapper = ResponseMapper<ConfirmTfaResponse>()
        return authApi.confirmTfaRegistration(jwtToken, tfaCode)
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

        return authApi.getSalutationList("EN")
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchCountryList(): Single<GetCountryListResponse> {

        return authApi.getCountryList("EN")
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun loginStep1(request: LoginWithTfaStep1Request): Single<LoginWithTfaStep1Response> {

        val mapper = ResponseMapper<LoginWithTfaStep1Response>()
        return authApi.loginWithTfaStep1(request.email, request.tfaCode)
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<LoginWithTfaStep1Response>() {

                    override fun handleSuccess(response: Response<LoginWithTfaStep1Response>): LoginWithTfaStep1Response {

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

    fun loginWithTfaStep2(jwtToken: String, request: LoginWithTfaStep2Request): Single<LoginWithTfaStep2Response> {

        val mapper = ResponseMapper<LoginWithTfaStep2Response>()
        return authApi.loginWithTfaStep2(jwtToken, request.publicKeyIndex188)
                .subscribeOn(Schedulers.newThread())
                .map(object : ResultMapper<LoginWithTfaStep2Response>() {

                    override fun handleSuccess(response: Response<LoginWithTfaStep2Response>): LoginWithTfaStep2Response {

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

    fun confirmMnemonic(jwtToken: String): Single<Unit> {
        return authApi.confirmMnemonic(jwtToken)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun resendConfirmationMail(request: ResendConfirmationMailRequest): Single<Any> {
        return authApi.resendConfirmationMail(request.email)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchRegistrationStatus(jwtToken: String): Single<GetRegistrationStatusResponse> {
        return authApi.getRegistrationStatus(jwtToken)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }
}