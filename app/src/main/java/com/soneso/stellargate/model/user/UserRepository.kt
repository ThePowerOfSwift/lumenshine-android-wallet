package com.soneso.stellargate.model.user

import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.domain.data.UserLogin
import com.soneso.stellargate.model.dto.DataProvider
import com.soneso.stellargate.model.dto.DataStatus
import com.soneso.stellargate.model.dto.ResponseObserver
import com.soneso.stellargate.model.dto.auth.RegistrationResponse
import com.soneso.stellargate.model.dto.auth.TfaRegistrationResponse
import com.soneso.stellargate.networking.UserRequester
import com.soneso.stellargate.persistence.SgPrefs
import okhttp3.Headers

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val userRequester: UserRequester, private val userDao: UserDao) {

    fun createUserAccount(account: Account): DataProvider<UserLogin> {

        val dataProvider = DataProvider<UserLogin>()

        val responseObserver = object : ResponseObserver<RegistrationResponse>() {

            override fun onSuccess(headers: Headers, body: RegistrationResponse?) {
                val data = body ?: return
                val jwtToken = headers.get("Authorization")!!
                val userLogin = UserLogin(account.email, jwtToken, data.token2fa)
                userDao.saveUserLogin(userLogin)
                dataProvider.data = userLogin
                dataProvider.status = DataStatus.SUCCESS
            }

            override fun onException(e: Throwable?) {
                dataProvider.status = DataStatus.ERROR
            }
        }

        dataProvider.status = DataStatus.LOADING
        userRequester.registerUser(account, responseObserver)
        return dataProvider
    }

    fun confirmTfaRegistration(tfaCode: String): DataProvider<Void> {

        val dataProvider = DataProvider<Void>()

        val responseObserver = object : ResponseObserver<TfaRegistrationResponse>() {

            override fun onSuccess(headers: Headers, body: TfaRegistrationResponse?) {
                dataProvider.status = DataStatus.SUCCESS
            }

            override fun onException(e: Throwable?) {
                dataProvider.status = DataStatus.ERROR
            }

        }

        val currentUserLogin = userDao.loadUserLogin(SgPrefs.username())
        dataProvider.status = DataStatus.LOADING
        userRequester.confirmTfaRegistration(currentUserLogin.jwtToken, tfaCode, responseObserver)

        return dataProvider
    }
}