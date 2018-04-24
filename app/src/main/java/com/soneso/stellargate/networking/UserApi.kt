package com.soneso.stellargate.networking

import com.soneso.stellargate.model.dto.auth.RegistrationResponse
import com.soneso.stellargate.model.dto.auth.TfaRegistrationResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


/**
 * Service used to retrofit.
 * Created by cristi.paval on 3/26/18.
 */
interface UserApi {

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
    ): Observable<Result<RegistrationResponse>>

    @FormUrlEncoded
    @POST("/ico/auth/confirm_tfa_registration")
    fun confirmTfaRegistration(
            @Header(SgApi.HEADER_NAME_AUTHORIZATION) jwtToken: String,
            @Field("tfa_code") tfaCode: String
    ): Observable<Result<TfaRegistrationResponse>>

}