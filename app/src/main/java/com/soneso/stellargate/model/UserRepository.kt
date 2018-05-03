package com.soneso.stellargate.model

import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.networking.dto.auth.*
import com.soneso.stellargate.networking.requester.AuthRequester
import com.soneso.stellargate.persistence.SgPrefs
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
                    userDao.insert(userSecurity)
                    userDao.insert(LoginSession(request.email, it.token2fa, it.jwtToken))
                    SgPrefs.currentUsername = request.email
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

        val request = LoginWithTfaStep2Request()
        request.publicKeyIndex188 = userSecurity.publicKeyIndex188

        return authRequester.loginWithTfaStep2(request)
                .map {
                    userDao.insert(userSecurity)
                    SgPrefs.currentUsername = userSecurity.username
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCurrentLoginSession(): Single<LoginSession> {
        return getLoginSession(SgPrefs.currentUsername)
    }

    fun getCurrentUserSecurity(): Single<UserSecurity?> {
        val username = SgPrefs.currentUsername
        return Single.just(userDao.loadUserSecurity(username))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun confirmMnemonic(): Single<Unit> {
        return authRequester.confirmMnemonic()
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun resendConfirmationMail(): Single<Unit> {

        val request = ResendConfirmationMailRequest()
        request.email = SgPrefs.currentUsername
        return authRequester.resendConfirmationMail(request)
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getRegistrationStatus(tfaCode: String): Single<RegistrationStatus> {

        val request = GetRegistrationStatusRequest()
        request.email = SgPrefs.currentUsername
        request.tfaCode = tfaCode
        return authRequester.fetchRegistrationStatus(request)
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