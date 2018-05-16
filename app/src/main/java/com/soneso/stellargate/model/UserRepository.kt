package com.soneso.stellargate.model

import android.util.Log
import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.networking.dto.auth.*
import com.soneso.stellargate.networking.requester.UserRequester
import com.soneso.stellargate.persistence.SgPrefs
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserRepository(private val userRequester: UserRequester) {

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

        return userRequester.registerUser(request)
                .map {

                    SgPrefs.jwtToken = it.jwtToken
                    SgPrefs.tfaSecret = it.token2fa
                    SgPrefs.username = request.email

                    RegistrationStatus(false, false, false)
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun confirmTfaRegistration(tfaCode: String): Single<RegistrationStatus> {

        return userRequester.confirmTfaRegistration(tfaCode)
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

        return userRequester.fetchSalutationList()
                .map {
                    return@map it.salutations
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getCountries(): Single<List<Country>> {

        return userRequester.fetchCountryList()
                .map {
                    it.countries
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun loginStep1(email: String, tfaCode: String? = null): Single<UserSecurity> {

        val request = LoginStep1Request()
        request.email = email
        request.tfaCode = tfaCode

        return userRequester.loginStep1(request)
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

        return userRequester.loginStep2(request)
                .map {
                    SgPrefs.jwtToken = it.jwtToken
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

    fun getCurrentUserSecurity(): Single<UserSecurity> {

        return userRequester.fetchUserSecurity()
                .map {
                    UserSecurity(
                            SgPrefs.username,
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

    fun confirmMnemonic(): Single<RegistrationStatus> {

        return userRequester.confirmMnemonic()
                .map {
                    RegistrationStatus(true, true, true)
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun resendConfirmationMail(): Single<Unit> {

        val request = ResendConfirmationMailRequest()
        request.email = SgPrefs.username
        return userRequester.resendConfirmationMail(request)
                .map {
                    Unit
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun getRegistrationStatus(): Single<RegistrationStatus> {

        return userRequester.fetchRegistrationStatus()
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

    fun requestEmailForPasswordReset(email: String): Single<Unit> {

        return userRequester.requestEmailForPasswordReset(email)
                .map {
                    Unit
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    fun requestEmailForTfaReset(email: String): Single<Unit> {

        return userRequester.requestEmailForTfaReset(email)
                .map {
                    Unit
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    private fun refreshTfaSecret(publicKey188: String) {

        val request = GetTfaSecretRequest()
        request.publicKey188 = publicKey188
        userRequester.fetchTfaSecret(request)
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

    fun changeUserPassword(userSecurity: UserSecurity): Single<Unit> {

        val request = ChangePasswordRequest()
        request.setPasswordKdfSalt(userSecurity.passwordKdfSalt)
        request.setEncryptedMnemonicMasterKey(userSecurity.encryptedMnemonicMasterKey)
        request.setMnemonicMasterKeyEncryptionIv(userSecurity.mnemonicMasterKeyEncryptionIv)
        request.setEncryptedWordListMasterKey(userSecurity.encryptedWordListMasterKey)
        request.setWordListMasterKeyEncryptionIv(userSecurity.wordListMasterKeyEncryptionIv)
        request.publicKeyIndex188 = userSecurity.publicKeyIndex188
        return userRequester.changeUserPassword(request)
                .map {
                    Unit
                }
                .onErrorResumeNext(SgError.singleFromNetworkException())
    }

    companion object {
        const val TAG = "UserRepository"
    }
}