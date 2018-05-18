package com.soneso.stellargate.networking.requester

import com.soneso.stellargate.networking.api.SgApi
import com.soneso.stellargate.networking.api.UserApi
import com.soneso.stellargate.networking.dto.ResponseMapper
import com.soneso.stellargate.networking.dto.ResultMapper
import com.soneso.stellargate.networking.dto.auth.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class UserRequester(private val userApi: UserApi) {

    fun registerUser(request: RegistrationRequest): Single<RegistrationResponse> {

        val mapper = ResponseMapper<RegistrationResponse>()
        return userApi.registerUser(
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
        return userApi.confirmTfaRegistration(tfaCode)
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

        return userApi.getSalutationList()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchCountryList(): Single<GetCountryListResponse> {

        return userApi.getCountryList()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun loginStep1(request: LoginStep1Request): Single<LoginStep1Response> {

        val mapper = ResponseMapper<LoginStep1Response>()
        return userApi.loginStep1(request.email, request.tfaCode)
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
        return userApi.loginStep2(request.publicKeyIndex188)
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
        return userApi.confirmMnemonic()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun resendConfirmationMail(request: ResendConfirmationMailRequest): Single<Any> {
        return userApi.resendConfirmationMail(request.email)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchRegistrationStatus(): Single<GetRegistrationStatusResponse> {
        return userApi.getRegistrationStatus()
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun requestEmailForPasswordReset(email: String): Single<Any> {
        return userApi.requestResetPasswordEmail(email)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun requestEmailForTfaReset(email: String): Single<Any> {
        return userApi.requestResetTfaEmail(email)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

    fun fetchTfaSecret(request: GetTfaSecretRequest): Single<GetTfaRequestResponse> {
        return userApi.getTfaSecret(request.publicKey188)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }

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

    fun changeTfaSecret(request: ChangeTfaSecretRequest): Single<ChangeTfaSecretResponse> {
        return userApi.changeTfaSecret(request.publicKeyIndex188)
                .subscribeOn(Schedulers.newThread())
                .map(ResponseMapper())
    }
}