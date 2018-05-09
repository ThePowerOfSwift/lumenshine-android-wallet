package com.soneso.stellargate.domain.usecases

import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.domain.util.Cryptor
import com.soneso.stellargate.domain.util.toCharArray
import com.soneso.stellargate.model.UserRepository
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.Single

/**
 * Manager.
 * Created by cristi.paval on 3/22/18.
 */
class AuthUseCases(private val userRepo: UserRepository) {

    private var password: CharSequence = ""

    fun registerAccount(email: CharSequence, password: CharSequence, country: Country?): Single<RegistrationStatus> {

        this.password = password
        val userProfile = UserProfile()
        userProfile.email = email.toString()
        userProfile.country = country

        val helper = UserSecurityHelper(password.toCharArray())
        val userSecurity = helper.generateUserSecurity(userProfile.email)

        return userRepo.createUserAccount(userProfile, userSecurity)
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
                    val publicKeyIndex188 = validateUserSecurity(password.toCharArray(), it)
                            ?: return@flatMap Single.error<RegistrationStatus>(SgError(R.string.login_password_wrong))
                    it.publicKeyIndex188 = publicKeyIndex188
                    userRepo.loginStep2(it)
                }
    }

    private fun decryptMnemonic(password: CharArray, userSecurity: UserSecurity): String {
        // cristi.paval, 4/27/18 - generate 256 bit password
        val derivedPassword = Cryptor.deriveKeyPbkdf2(userSecurity.passwordKdfSalt, password)


        // cristi.paval, 4/27/18 - decrypt master key
        val masterKey = Cryptor.decryptValue(derivedPassword, userSecurity.encryptedMasterKey, userSecurity.masterKeyEncryptionIv)

        // cristi.paval, 4/27/18 - decrypt mnemonic
        val paddedMnemonic = Cryptor.decryptValue(masterKey, userSecurity.encryptedMnemonic, userSecurity.mnemonicEncryptionIv)
        return String(Cryptor.removePadding(paddedMnemonic), charset("UTF-8"))
    }

    /**
     * @return public key index 188 if valid, null otherwise
     */
    private fun validateUserSecurity(password: CharArray, userSecurity: UserSecurity): String? {

        val mnemonic = decryptMnemonic(password, userSecurity)

        if (mnemonic.split(" ").size != 24) {
            return null
        }

        // cristi.paval, 4/27/18 - generate public keys
        val mnemonicChars = mnemonic.toCharArray()
        val publicKeyIndex0 = Wallet.createKeyPair(mnemonicChars, null, 0).accountId
        if (publicKeyIndex0 != userSecurity.publicKeyIndex0) {
            return null
        }

        return Wallet.createKeyPair(mnemonicChars, null, 188).accountId
    }

    fun provideMnemonicForCurrentUser(): Single<String> {
        return userRepo.getCurrentUserSecurity()
                .map {
                    decryptMnemonic(password.toCharArray(), it)
                }
    }

    fun confirmMnemonic() = userRepo.confirmMnemonic()

    fun resendConfirmationMail() = userRepo.resendConfirmationMail()

    fun provideRegistrationStatus() = userRepo.getRegistrationStatus()

    fun provideTfaSecret() = userRepo.getTfaSecret()

    companion object {
        const val TAG = "AuthUseCases"
    }
}