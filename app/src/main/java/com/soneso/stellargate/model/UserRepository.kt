package com.soneso.stellargate.model

import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.model.dto.*
import com.soneso.stellargate.model.dto.auth.RegistrationResponse
import com.soneso.stellargate.model.dto.auth.TfaRegistrationResponse
import com.soneso.stellargate.networking.AuthRequester
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.persistence.UserDao
import io.reactivex.Single
import okhttp3.Headers

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val authRequester: AuthRequester, private val userDao: UserDao) {

    fun createUserAccount(account: Account): DataProvider<UserLogin> {

        val dataProvider = DataProvider<UserLogin>()
        val responseObserver = object : ResponseObserver<RegistrationResponse>() {

            override fun onSuccess(headers: Headers, body: RegistrationResponse?) {
                val data = body ?: return

                SgPrefs.username = account.email
                val jwtToken = headers.get("Authorization")!!

                val userLogin = UserLogin(account.email, jwtToken, data.token2fa)
                userDao.saveUserLogin(userLogin)
                dataProvider.data = userLogin
                dataProvider.status = DataStatus.SUCCESS
            }

            override fun onError(error: SgNetworkError) {
                dataProvider.error = SgError.fromNetworkError(error)
                dataProvider.status = DataStatus.ERROR
            }
        }

        dataProvider.status = DataStatus.LOADING
        authRequester.registerUser(account, responseObserver)
        return dataProvider
    }

    fun confirmTfaRegistration(tfaCode: String): DataProvider<Void> {

        val dataProvider = DataProvider<Void>()

        val responseObserver = object : ResponseObserver<TfaRegistrationResponse>() {

            override fun onSuccess(headers: Headers, body: TfaRegistrationResponse?) {
                dataProvider.status = DataStatus.SUCCESS
            }

            override fun onError(error: SgNetworkError) {
                dataProvider.error = SgError.fromNetworkError(error)
                dataProvider.status = DataStatus.ERROR
            }
        }

        dataProvider.status = DataStatus.LOADING
        authRequester.confirmTfaRegistration(tfaCode, responseObserver)

        return dataProvider
    }

    fun getSalutations(): Single<List<String>> {
        return authRequester.fetchSalutationList()
                .map { salutationListResponse ->
                    return@map salutationListResponse.salutations
                }
                .onErrorResumeNext {
                    val networkException = it as SgNetworkException
                    Single.error(SgError.fromNetworkException(networkException))
                }
    }
}