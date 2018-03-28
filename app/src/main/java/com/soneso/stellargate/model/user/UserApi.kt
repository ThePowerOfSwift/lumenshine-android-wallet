package com.soneso.stellargate.model.user

import com.soneso.stellargate.model.dto.RegistrationRequest
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Service used to retrofit.
 * Created by cristi.paval on 3/26/18.
 */
interface UserApi {

    @POST("/registration")
    fun registerUser(@Body body: RegistrationRequest): Observable<Void>
}