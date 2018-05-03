package com.soneso.stellargate.domain.usecases

import android.util.Log
import com.google.authenticator.OtpProvider
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.domain.util.Cryptor
import com.soneso.stellargate.domain.util.toByteArray
import com.soneso.stellargate.domain.util.toCharArray
import com.soneso.stellargate.model.UserRepository
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.Single
import org.bouncycastle.util.encoders.Base64

/**
 * Manager.
 * Created by cristi.paval on 3/22/18.
 */
class AuthUseCases(private val userRepo: UserRepository) {

    fun generateAccount(email: CharSequence, password: CharSequence, country: Country?): Single<String> {

        val userProfile = UserProfile()
        userProfile.email = email.toString()
        userProfile.country = country

        val userSecurity = createUserSecurity(userProfile.email, password.toCharArray())

        return userRepo.createUserAccount(userProfile, userSecurity)
    }

    //password, kdf salt, kdf password, master key, master key iv, encrypted master key, nmemonic, mnemonic iv, encrypted mnemonic
    private fun createUserSecurity(email: String, pass: CharArray): UserSecurity {

        // cristi.paval, 3/23/18 - generate 256 bit password and salt
        val passwordSalt = Cryptor.generateSalt()
        val derivedPassword = Cryptor.deriveKeyPbkdf2(passwordSalt, pass)

        // cristi.paval, 3/23/18 - generate master key
        val masterKey = Cryptor.generateMasterKey()

        // cristi.paval, 3/23/18 - encrypt master key
        val (encryptedMasterKey, masterKeyIv) = Cryptor.encryptValue(masterKey, derivedPassword)


        // cristi.paval, 3/23/18 - generate mnemonic
        val mnemonic = Wallet.generate24WordMnemonic()

        // cristi.paval, 3/23/18 - encrypt the mnemonic
        val adjustedMnemonic = Cryptor.applyPadding(16, mnemonic.toByteArray())
        val (encryptedMnemonic, mnemonicIv) = Cryptor.encryptValue(adjustedMnemonic, masterKey)

        // cristi.paval, 3/23/18 - generate public keys
        val publicKeyIndex0 = Wallet.createKeyPair(mnemonic, null, 0).accountId

        val publicKeyIndex188 = Wallet.createKeyPair(mnemonic, null, 188).accountId

        val userSecurity = UserSecurity(
                email,
                publicKeyIndex0,
                publicKeyIndex188,
                passwordSalt,
                encryptedMasterKey,
                masterKeyIv,
                encryptedMnemonic,
                mnemonicIv
        )

        if (BuildConfig.DEBUG) {
            logSecurityData(pass, derivedPassword, masterKey, mnemonic, userSecurity)
        }

        return userSecurity
    }

    private fun logSecurityData(
            pass: CharArray,
            derivedPassword: ByteArray,
            masterKey: ByteArray,
            mnemonic: CharArray,
            userSecurity: UserSecurity
    ) {
        Log.d(TAG, "password: ${String(pass)}")

        val encodedPassSalt = Base64.toBase64String(userSecurity.passwordKdfSalt)
        Log.d(TAG, "kdf salt: $encodedPassSalt \t\t\t\tlength: ${encodedPassSalt.length}")

        val encodedKdfPass = Base64.toBase64String(derivedPassword)
        Log.d(TAG, "kdf password: $encodedKdfPass \t\t\t\tlength: ${encodedKdfPass.length}")

        val encodedMasterKey = Base64.toBase64String(masterKey)
        Log.d(TAG, "master key: $encodedMasterKey \t\t\t\tlength: ${encodedMasterKey.length}")

        val encryptedMasterKeyEncoded = Base64.toBase64String(userSecurity.encryptedMasterKey)
        Log.d(TAG, "encrypted master key: $encryptedMasterKeyEncoded \t\t\t\tlength: ${encryptedMasterKeyEncoded.length}")

        val masterKeyIvEncoded = Base64.toBase64String(userSecurity.masterKeyEncryptionIv)
        Log.d(TAG, "master key iv: $masterKeyIvEncoded \t\t\t\tlength: ${masterKeyIvEncoded.length}")

        Log.d(TAG, "mnemonic: ${String(mnemonic)}")

        val encryptedMnemonicEncoded = Base64.toBase64String(userSecurity.encryptedMnemonic)
        Log.d(TAG, "encrypted mnemonic: $encryptedMnemonicEncoded length: ${encryptedMnemonicEncoded.length}")

        val mnemonicIvEncoded = Base64.toBase64String(userSecurity.mnemonicEncryptionIv)
        Log.d(TAG, "mnemonic iv: $mnemonicIvEncoded \t\t\t\tlength: ${mnemonicIvEncoded.length}")
    }

    fun confirmTfaRegistration(tfaCode: String) = userRepo.confirmTfaRegistration(tfaCode)

    fun provideSalutations() = userRepo.getSalutations()

    fun provideCountries() = userRepo.getCountries()

    fun login(email: CharSequence, password: CharSequence, tfaCode: CharSequence): Single<RegistrationStatus> {

        return if (tfaCode.isBlank()) {
            userRepo.getLoginSession(email.toString())
                    .flatMap {
                        val code = OtpProvider.currentTotpCode(it.tfaSecret)
                        loginWithCredentials(email.toString(), password.toString(), code)
                    }
                    .onErrorResumeNext {
                        loginWithCredentials(email.toString(), password.toString())
                    }
        } else {
            loginWithCredentials(email.toString(), password.toString(), tfaCode.toString())
        }
    }

    private fun loginWithCredentials(email: String, password: String, tfaCode: String? = null): Single<RegistrationStatus> {

        var error: SgError? = null
        return userRepo.loginWithTfaStep1(email, tfaCode)
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
                    userRepo.loginWithTfaStep2(it)
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
        if (publicKeyIndex0 != userSecurity.publicKeyIndex0) { // GA4I2AGEGIBA2ZO5RGTW4KIZATXQX3GXN6KXR7K3FFOUDNTRO63OJGKL
            return null // obtain cover shy swift antique suggest talk mercy half ice clean kidney hip coach cage holiday embark kite noise peace inspire scorpion journey bunker
        }

        return Wallet.createKeyPair(mnemonicChars, null, 188).accountId
    }

    fun provideMnemonicForCurrentUser(password: CharSequence): Single<String> {
        return userRepo.getCurrentUserSecurity()
                .map {
                    decryptMnemonic(password.toCharArray(), it)
                }
    }

    fun confirmMnemonic() = userRepo.confirmMnemonic()

    fun resendConfirmationMail() = userRepo.resendConfirmationMail()

    fun provideRegistrationStatus(): Single<RegistrationStatus> {
        return userRepo.getCurrentLoginSession()
                .flatMap {
                    val tfaCode = OtpProvider.currentTotpCode(it.tfaSecret)
                            ?: throw SgError(R.string.unknown_error)
                    userRepo.getRegistrationStatus(tfaCode)
                }
    }

    companion object {
        const val TAG = "AuthUseCases"
    }
}