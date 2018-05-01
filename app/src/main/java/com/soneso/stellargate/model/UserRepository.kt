package com.soneso.stellargate.model

import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.networking.dto.auth.LoginWithTfaStep1Request
import com.soneso.stellargate.networking.dto.auth.LoginWithTfaStep2Request
import com.soneso.stellargate.networking.dto.auth.RegistrationRequest
import com.soneso.stellargate.networking.requester.AuthRequester
import com.soneso.stellargate.persistence.UserDao
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val authRequester: AuthRequester, private val userDao: UserDao) {

    fun createUserAccount(userProfile: UserProfile, userSecurity: UserSecurity): Single<String> {

        val request = RegistrationRequest()
        request.email = userProfile.email
        request.countryCode = userProfile.country?.code
        request.publicKeyIndex0 = userSecurity.publicKeyIndex0
        request.publicKeyIndex188 = userSecurity.publicKeyIndex188
        request.setPasswordKdfSalt(userSecurity.passwordKdfSalt)
        request.setEncryptedMasterKey(userSecurity.encryptedMasterKey)
        request.setMasterKeyEncryptionIv(userSecurity.masterKeyEncryptionIv)
        request.setEncryptedMnemonic(userSecurity.encryptedMnemonic)
        request.setMnemonicEncryptionIv(userSecurity.mnemonicEncryptionIv)

        return authRequester.registerUser(request)
                .map {
                    userDao.insert(LoginSession(request.email, it.token2fa, it.jwtToken))
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

    fun loginWithTfaStep1(email: String, tfaCode: String?): Single<UserSecurity> {

        val request = LoginWithTfaStep1Request()
        request.email = email
        request.tfaCode = tfaCode

        return authRequester.loginWithTfaStep1(request)
                .map {
                    userDao.updateLoginSession(email, it.jwtToken)

                    UserSecurity(
                            it.publicKeyIndex0,
                            "",
                            it.passwordKdfSalt(),
                            it.encryptedMasterKey(),
                            it.masterKeyEncryptionIv(),
                            it.encryptedMnemonic(),
                            it.mnemonicEncryptionIv()
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun loginWithTfaStep2(userSecurity: UserSecurity): Single<DashboardStatus> {

        val request = LoginWithTfaStep2Request()
        request.publicKeyIndex188 = userSecurity.publicKeyIndex188

        return authRequester.loginWithTfaStep2(request)
                .map {
                    DashboardStatus(
                            it.emailConfirmed,
                            it.mnemonicConfirmed
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getLoginSession(username: String): Single<LoginSession?> {
        return Single.just(userDao.loadLoginSession(username))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }
}