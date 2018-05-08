package com.soneso.stellargate.model

import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.networking.dto.auth.LoginWithTfaStep1Request
import com.soneso.stellargate.networking.dto.auth.LoginWithTfaStep2Request
import com.soneso.stellargate.networking.dto.auth.RegistrationRequest
import com.soneso.stellargate.networking.dto.auth.ResendConfirmationMailRequest
import com.soneso.stellargate.networking.requester.AuthRequester
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.persistence.UserDao
import io.reactivex.Single
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
                    SgPrefs.currentUsername = request.email
                    userDao.insert(userSecurity)
                    userDao.insert(LoginSession(userProfile.email, userProfile.password, it.token2fa, it.jwtToken))
                    it.token2fa
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun confirmTfaRegistration(tfaCode: String): Single<RegistrationStatus> {

        return getCurrentLoginSession()
                .flatMap {
                    authRequester.confirmTfaRegistration(it.jwtToken, tfaCode)
                }
                .map {

                    userDao.updateJwtToken(SgPrefs.currentUsername, it.jwtToken)
                    RegistrationStatus(
                            it.emailConfirmed,
                            it.mnemonicConfirmed,
                            it.tfaConfirmed
                    )
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

    fun loginStep1(email: String, password: String, tfaCode: String?): Single<UserSecurity> {

        val request = LoginWithTfaStep1Request()
        request.email = email
        request.tfaCode = tfaCode

        return authRequester.loginStep1(request)
                .map {

                    SgPrefs.currentUsername = email
                    userDao.insert(LoginSession(email, password, "", it.jwtToken))

                    UserSecurity(
                            email,
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

    fun loginWithTfaStep2(userSecurity: UserSecurity): Single<RegistrationStatus> {

        return getCurrentLoginSession()
                .flatMap {
                    val request = LoginWithTfaStep2Request()
                    request.publicKeyIndex188 = userSecurity.publicKeyIndex188

                    authRequester.loginWithTfaStep2(it.jwtToken, request)
                }
                .map {
                    userDao.insert(userSecurity)
                    userDao.updateJwtToken(userSecurity.username, it.jwtToken)
                    if (it.tfaSecret.isNotEmpty()) {
                        userDao.updateTfaSecret(userSecurity.username, it.tfaSecret)
                    }
                    RegistrationStatus(
                            it.emailConfirmed,
                            it.mnemonicConfirmed,
                            it.tfaConfirmed
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getLoginSession(username: String): Single<LoginSession> {
        return Single.just(userDao.loadLoginSession(username) ?: LoginSession())
                .map { ls ->
                    if (ls.username.isEmpty()) {
                        throw SgError(R.string.unauthorized_user)
                    }
                    ls
                }
                .subscribeOn(Schedulers.newThread())
    }

    fun getCurrentLoginSession(): Single<LoginSession> {
        return getLoginSession(SgPrefs.currentUsername)
    }

    fun getCurrentUserSecurity(): Single<UserSecurity?> {
        val username = SgPrefs.currentUsername
        return Single.just(userDao.loadUserSecurity(username))
                .subscribeOn(Schedulers.newThread())
    }

    fun confirmMnemonic(): Single<Unit> {
        return getCurrentLoginSession()
                .flatMap {
                    authRequester.confirmMnemonic(it.jwtToken)
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun resendConfirmationMail(): Single<Any> {

        val request = ResendConfirmationMailRequest()
        request.email = SgPrefs.currentUsername
        return authRequester.resendConfirmationMail(request)
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getRegistrationStatus(): Single<RegistrationStatus> {

        return getCurrentLoginSession()
                .flatMap {
                    authRequester.fetchRegistrationStatus(it.jwtToken)
                }
                .map {
                    RegistrationStatus(
                            it.mailConfirmed,
                            it.mnemonicConfirmed,
                            it.tfaConfirmed
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }
}