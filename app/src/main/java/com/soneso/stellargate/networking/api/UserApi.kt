package com.soneso.stellargate.networking.api

import com.soneso.stellargate.networking.dto.auth.*
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


/**
 * Service used to retrofit.
 * Created by cristi.paval on 3/26/18.
 */
interface UserApi {


    @FormUrlEncoded
    @POST("/portal/user/register_user")
    fun registerUser(
            @Field("email") email: String,

            @Field("kdf_salt") passwordSalt: String,

            @Field("mnemonic_master_key") encryptedMnemonicMasterKey: String,
            @Field("mnemonic_master_iv") mnemonicMasterKeyIv: String,

            @Field("mnemonic") encryptedMnemonic: String,
            @Field("mnemonic_iv") mnemonicIv: String,

            @Field("wordlist_master_key") encryptedWordListMasterKey: String,
            @Field("wordlist_master_iv") wordListMasterKeyEncryptionIv: String,

            @Field("wordlist") encryptedWordList: String,
            @Field("wordlist_iv") wordListEncryptionIv: String,

            @Field("public_key_0") publicKey0: String,
            @Field("public_key_188") publicKey188: String,
            @Field("country_code") countryCode: String?

    ): Single<Result<RegistrationResponse>>


    @FormUrlEncoded
    @POST("/portal/user/auth/confirm_tfa_registration")
    fun confirmTfaRegistration(
            @Field("tfa_code") tfaCode: String
    ): Single<Result<ConfirmTfaResponse>>


    @GET("/portal/user/salutation_list")
    fun getSalutationList(): Single<Result<GetSalutationListResponse>>


    @GET("/portal/user/country_list")
    fun getCountryList(): Single<Result<GetCountryListResponse>>


    @FormUrlEncoded
    @POST("/portal/user/login_step1")
    fun loginStep1(
            @Field("email") email: String,
            @Field("tfa_code") tfaCode: String?
    ): Single<Result<LoginStep1Response>>


    @FormUrlEncoded
    @POST("/portal/user/auth/login_step2")
    fun loginStep2(
            @Field("key") publicKey188: String
    ): Single<Result<LoginStep2Response>>


    @POST("/portal/user/dashboard/confirm_mnemonic")
    fun confirmMnemonic(): Single<Result<Any>>


    @FormUrlEncoded
    @POST("/portal/user/resend_confirmation_mail")
    fun resendConfirmationMail(
            @Field("email") email: String
    ): Single<Result<Any>>


    @GET("/portal/user/dashboard/get_user_registration_status")
    fun getRegistrationStatus(): Single<Result<GetRegistrationStatusResponse>>

    @FormUrlEncoded
    @POST("/portal/user/lost_password")
    fun requestResetPasswordEmail(
            @Field("email") email: String
    ): Single<Result<Any>>

    @FormUrlEncoded
    @POST("/portal/user/lost_tfa")
    fun requestResetTfaEmail(
            @Field("email") email: String
    ): Single<Result<Any>>

    @FormUrlEncoded
    @POST("/portal/user/dashboard/tfa_secret")
    fun getTfaSecret(
            @Field("key") publicKey188: String
    ): Single<Result<GetTfaRequestResponse>>

    @GET("/portal/user/dashboard/user_auth_data")
    fun getUserAuthData(): Single<Result<GetUserAuthDataResponse>>
}