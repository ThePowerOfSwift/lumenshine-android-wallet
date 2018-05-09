package com.soneso.stellargate.model

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
                    SgPrefs.username = request.email
                    SgPrefs.jwtToken = it.jwtToken
                    SgPrefs.tfaSecret = it.token2fa
                    userDao.insert(userSecurity)

                    it.token2fa
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun confirmTfaRegistration(tfaCode: String): Single<RegistrationStatus> {

        return authRequester.confirmTfaRegistration(tfaCode)
                .map {
                    SgPrefs.jwtToken = it.jwtToken

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

    fun loginStep1(email: String, tfaCode: String?): Single<UserSecurity> {

        val request = LoginWithTfaStep1Request()
        request.email = email
        request.tfaCode = tfaCode

        return authRequester.loginStep1(request)
                .map {

                    SgPrefs.username = email
                    SgPrefs.jwtToken = it.jwtToken

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
                    SgPrefs.jwtToken = it.jwtToken
                    userDao.insert(userSecurity)
                    // TODO: cristi.paval, 5/9/18 - finish here
//                    if (it.tfaSecret.isNotEmpty()) {
//                        userDao.updateTfaSecret(userSecurity.username, it.tfaSecret)
//                    }

                    RegistrationStatus(
                            it.emailConfirmed,
                            it.mnemonicConfirmed,
                            it.tfaConfirmed
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getCurrentUserSecurity(): Single<UserSecurity?> {
        val username = SgPrefs.username
        return Single.just(userDao.loadUserSecurity(username))
                .subscribeOn(Schedulers.newThread())
    }

    fun confirmMnemonic(): Single<Unit> {
        return authRequester.confirmMnemonic()
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun resendConfirmationMail(): Single<Any> {

        val request = ResendConfirmationMailRequest()
        request.email = SgPrefs.username
        return authRequester.resendConfirmationMail(request)
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getRegistrationStatus(): Single<RegistrationStatus> {

        return authRequester.fetchRegistrationStatus()
                .map {
                    RegistrationStatus(
                            it.mailConfirmed,
                            it.mnemonicConfirmed,
                            it.tfaConfirmed
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getTfaSecret(): Single<String> {
        return Single.just(SgPrefs.tfaSecret)
                .subscribeOn(Schedulers.newThread())
    }
}