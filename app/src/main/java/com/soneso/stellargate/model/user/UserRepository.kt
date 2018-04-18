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

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val userRequester: UserRequester, private val userDao: UserDao) {

    fun createUserAccount(account: Account): DataProvider<Account> {

        val dataProvider = DataProvider<Account>()

        val responseObserver = object : ResponseObserver<RegistrationResponse>() {

            override fun onResponse(data: RegistrationResponse) {

                userDao.saveUserLogin(UserLogin(account.email, data.jwtToken, data.token2fa))
                dataProvider.data = account
                dataProvider.status = DataStatus.SUCCESS
            }

            override fun onException(e: Throwable) {

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
            override fun onResponse(data: TfaRegistrationResponse) {
                dataProvider.status = DataStatus.SUCCESS
            }

            override fun onException(e: Throwable) {
                dataProvider.status = DataStatus.ERROR
            }

        }

        val currentUserLogin = userDao.loadUserLogin(SgPrefs.username())
        dataProvider.status = DataStatus.LOADING
        userRequester.confirmTfaRegistration(currentUserLogin.jwtToken, tfaCode, responseObserver)

        return dataProvider
    }
}