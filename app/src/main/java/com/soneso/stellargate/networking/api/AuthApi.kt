package com.soneso.stellargate.networking.api

import com.soneso.stellargate.networking.dto.auth.GetSalutationListResponse
import com.soneso.stellargate.networking.dto.auth.RegistrationResponse
import com.soneso.stellargate.networking.dto.auth.TfaRegistrationResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.*


/**
 * Service used to retrofit.
 * Created by cristi.paval on 3/26/18.
 */
interface AuthApi {

    @FormUrlEncoded
    @POST("/ico/register_user")
    fun registerUser(
            @Field("email") email: String,
            @Field("kdf_salt") passwordSalt: String,
            @Field("master_key") encryptedMasterKey: String,
            @Field("master_iv") masterKeyIv: String,
            @Field("mnemonic") encryptedMnemonic: String,
            @Field("mnemonic_iv") mnemonicIv: String,
            @Field("public_key_0") publicKey0: String,
            @Field("public_key_188") publicKey188: String
    ): Single<Result<RegistrationResponse>>

    @FormUrlEncoded
    @POST("/ico/auth/confirm_tfa_registration")
    fun confirmTfaRegistration(
            @Header(SgApi.HEADER_NAME_AUTHORIZATION) jwtToken: String,
            @Field("tfa_code") tfaCode: String
    ): Single<Result<TfaRegistrationResponse>>

    @GET("/ico/salutation_list/{${SgApi.URL_PARAM_LANG}}")
    fun getSalutationList(@Path(SgApi.URL_PARAM_LANG) langKey: String): Single<Result<GetSalutationListResponse>>
}