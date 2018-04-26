package com.soneso.stellargate.model

import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.domain.data.Country
import com.soneso.stellargate.domain.data.SgError
import com.soneso.stellargate.domain.data.singleFromNetworkException
import com.soneso.stellargate.networking.requester.AuthRequester
import com.soneso.stellargate.persistence.UserDao
import io.reactivex.Single

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val authRequester: AuthRequester, private val userDao: UserDao) {

    fun createUserAccount(account: Account): Single<String> {

        return authRequester.registerUser(account)
                .map {
                    it.token2fa
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun confirmTfaRegistration(tfaCode: String): Single<Unit> {

        return authRequester.confirmTfaRegistration(tfaCode)
                .map {
                    Unit
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getSalutations(): Single<List<String>> {
        return authRequester.fetchSalutationList()
                .map {
                    return@map it.salutations
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getCountries(): Single<List<Country>> {
        return authRequester.fetchCountryList()
                .map {
                    it.countries
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }
}