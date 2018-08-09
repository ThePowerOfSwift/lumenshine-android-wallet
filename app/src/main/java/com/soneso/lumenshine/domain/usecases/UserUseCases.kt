package com.soneso.lumenshine.domain.usecases

import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.*
import com.soneso.lumenshine.domain.util.toCharArray
import com.soneso.lumenshine.model.UserRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Manager.
 * Created by cristi.paval on 3/22/18.
 */
class UserUseCases(private val userRepo: UserRepository) {

    private var password: CharSequence = ""

    fun registerAccount(email: CharSequence, password: CharSequence, country: Country?): Single<RegistrationStatus> {

        this.password = password
        val userProfile = UserProfile()
        userProfile.email = email.toString()
        userProfile.country = country

        val helper = UserSecurityHelper(password.toCharArray())
        return Single
                .create<UserSecurity> {
                    it.onSuccess(helper.generateUserSecurity(userProfile.email))
                }
                .subscribeOn(Schedulers.newThread())
                .flatMap { userRepo.createUserAccount(userProfile, it) }
    }

    fun confirmTfaRegistration(tfaCode: String) = userRepo.confirmTfaRegistration(tfaCode)

    fun provideSalutations() = userRepo.getSalutations()

    fun provideCountries() = userRepo.getCountries()

    fun login(email: CharSequence, password: CharSequence, tfaCode: CharSequence?): Single<RegistrationStatus> {

        this.password = password.toString()
        var error: SgError? = null

        return userRepo.loginStep1(email.toString(), tfaCode?.toString())
                .onErrorResumeNext {
                    error = it as SgError
                    Single.just(UserSecurity.mockInstance())
                }
                .flatMap {
                    if (it.username.isEmpty()) {
                        // cristi.paval, 4/27/18 - is mocked instance
                        return@flatMap Single.error<RegistrationStatus>(error
                                ?: SgError(R.string.unknown_error))
                    }

                    val helper = UserSecurityHelper(password.toCharArray())
                    val publicKeyIndex188 = helper.decipherUserSecurity(it)
                            ?: return@flatMap Single.error<RegistrationStatus>(SgError(R.string.login_password_wrong))
                    it.publicKeyIndex188 = publicKeyIndex188
                    userRepo.loginStep2(it)
                }
    }

    fun provideMnemonicForCurrentUser(): Single<String> {
        return userRepo.getCurrentUserSecurity()
                .map {
                    val helper = UserSecurityHelper(password.toCharArray())
                    helper.decipherUserSecurity(it)
                    String(helper.mnemonicChars)
                }
    }

    fun confirmMnemonic() = userRepo.confirmMnemonic()

    fun resendConfirmationMail() = userRepo.resendConfirmationMail()

    fun provideRegistrationStatus() = userRepo.getRegistrationStatus()

    fun provideTfaSecret() = userRepo.getTfaSecret()

    fun requestPasswordReset(email: String) = userRepo.requestEmailForPasswordReset(email)

    fun requestTfaReset(email: String) = userRepo.requestEmailForTfaReset(email)

    fun provideLastUserCredentials() = userRepo.getLastUserCredentials()

    fun changeUserPassword(currentPass: CharSequence, newPass: CharSequence): Single<Unit> {

        return userRepo.getCurrentUserSecurity()
                .flatMap {
                    val helper = UserSecurityHelper(currentPass.toCharArray())
                    val us = helper.changePassword(it, newPass.toCharArray())
                    userRepo.changeUserPassword(us)
                }
    }

    fun changeTfaPassword(pass: CharSequence): Single<TfaSecret> {

        return userRepo.getCurrentUserSecurity()
                .flatMap {
                    val helper = UserSecurityHelper(pass.toCharArray())
                    val publicKey188 = helper.decipherUserSecurity(it)
                    if (publicKey188 != null) {
                        userRepo.changeTfaSecret(publicKey188)
                    } else {
                        Single.error(SgError(R.string.login_password_wrong))
                    }
                }
    }

    fun confirmTfaSecretChange(tfaCode: CharSequence) = userRepo.confirmTfaSecretChange(tfaCode.toString())

    companion object {

        const val TAG = "UserUseCases"
    }
}