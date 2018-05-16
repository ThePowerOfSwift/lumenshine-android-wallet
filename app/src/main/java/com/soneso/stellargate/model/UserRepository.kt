package com.soneso.stellargate.model

import android.util.Log
import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.networking.dto.auth.*
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

    fun createUserAccount(userProfile: UserProfile, userSecurity: UserSecurity): Single<RegistrationStatus> {

        val request = RegistrationRequest()
        request.email = userProfile.email
        request.countryCode = userProfile.country?.code
        request.publicKeyIndex0 = userSecurity.publicKeyIndex0
        request.publicKeyIndex188 = userSecurity.publicKeyIndex188

        request.setPasswordKdfSalt(userSecurity.passwordKdfSalt)

        request.setEncryptedMnemonicMasterKey(userSecurity.encryptedMnemonicMasterKey)
        request.setMnemonicMasterKeyEncryptionIv(userSecurity.mnemonicMasterKeyEncryptionIv)

        request.setEncryptedMnemonic(userSecurity.encryptedMnemonic)
        request.setMnemonicEncryptionIv(userSecurity.mnemonicEncryptionIv)

        request.setEncryptedWordListMasterKey(userSecurity.encryptedWordListMasterKey)
        request.setWordListMasterKeyEncryptionIv(userSecurity.wordListMasterKeyEncryptionIv)

        request.setEncryptedWordList(userSecurity.encryptedWordList)
        request.setWordListEncryptionIv(userSecurity.wordListEncryptionIv)

        return authRequester.registerUser(request)
                .map {

                    SgPrefs.jwtToken = it.jwtToken
                    SgPrefs.tfaSecret = it.token2fa
                    SgPrefs.username = request.email
                    userDao.insert(userSecurity)

                    RegistrationStatus(false, false, false)
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

    fun loginStep1(email: String, tfaCode: String? = null): Single<UserSecurity> {

        val request = LoginStep1Request()
        request.email = email
        request.tfaCode = tfaCode

        return authRequester.loginStep1(request)
                .map {

                    SgPrefs.jwtToken = it.jwtToken
                    SgPrefs.username = email

                    UserSecurity(
                            email,
                            it.publicKeyIndex0,
                            "",
                            it.passwordKdfSalt(),
                            it.encryptedMnemonicMasterKey(),
                            it.mnemonicMasterKeyEncryptionIv(),
                            it.encryptedMnemonic(),
                            it.mnemonicEncryptionIv(),
                            it.encryptedWordListMasterKey(),
                            it.wordListMasterKeyEncryptionIv(),
                            it.encryptedWordList(),
                            it.wordListEncryptionIv()
                    )
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun loginStep2(userSecurity: UserSecurity): Single<RegistrationStatus> {

        val request = LoginStep2Request()
        request.publicKeyIndex188 = userSecurity.publicKeyIndex188

        return authRequester.loginStep2(request)
                .map {
                    SgPrefs.jwtToken = it.jwtToken
                    userDao.insert(userSecurity)
                    if (it.tfaSecret.isNotEmpty()) {
                        SgPrefs.tfaSecret = it.tfaSecret
                    } else {
                        refreshTfaSecret(userSecurity.publicKeyIndex188)
                    }

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
        return Single
                .create<UserSecurity> {
                    val us = userDao.loadUserSecurity(username) ?: UserSecurity.mockInstance()
                    it.onSuccess(us)
                }
                .subscribeOn(Schedulers.newThread())
    }

    fun confirmMnemonic(): Single<RegistrationStatus> {

        return authRequester.confirmMnemonic()
                .map {
                    RegistrationStatus(true, true, true)
                }
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

        return Single
                .create<String> { it.onSuccess(SgPrefs.tfaSecret) }
                .subscribeOn(Schedulers.newThread())
    }

    fun requestEmailForPasswordReset(email: String): Single<Any> {

        return authRequester.requestEmailForPasswordReset(email)
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun requestEmailForTfaReset(email: String): Single<Any> {

        return authRequester.requestEmailForTfaReset(email)
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    private fun refreshTfaSecret(publicKey188: String) {

        val request = GetTfaSecretRequest()
        request.publicKey188 = publicKey188
        authRequester.fetchTfaSecret(request)
                .map {
                    it.tfaSecret
                }
                .subscribe({
                    SgPrefs.tfaSecret = it
                }, {
                    Log.e(TAG, "Error", it)
                })
    }

    fun getLastUserCredentials(): Single<UserCredentials> {
        return Single
                .create<UserCredentials> {
                    val uc = UserCredentials(SgPrefs.username, SgPrefs.tfaSecret)
                    it.onSuccess(uc)
                }
                .subscribeOn(Schedulers.newThread())
    }

    companion object {
        const val TAG = "UserRepository"
    }
}