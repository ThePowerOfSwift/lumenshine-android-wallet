package com.soneso.stellargate.domain.usecases

import android.util.Log
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.*
import com.soneso.stellargate.domain.util.Cryptor
import com.soneso.stellargate.domain.util.toCharArray
import com.soneso.stellargate.model.UserRepository
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.util.PrimitiveUtil
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

        val userSecurity = createUserSecurity(password.toCharArray())

        return userRepo.createUserAccount(userProfile, userSecurity)
    }

    //password, kdf salt, kdf password, master key, master key iv, encrypted master key, nmemonic, mnemonic iv, encrypted mnemonic
    private fun createUserSecurity(pass: CharArray): UserSecurity {

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
        val adjustedMnemonic = Cryptor.applyPadding(16, PrimitiveUtil.toBytes(mnemonic))
        val (encryptedMnemonic, mnemonicIv) = Cryptor.encryptValue(adjustedMnemonic, masterKey)

        // cristi.paval, 3/23/18 - generate public keys
        val publicKeyIndex0 = Wallet.createKeyPair(mnemonic, null, 0).accountId
        val publicKeyIndex188 = Wallet.createKeyPair(mnemonic, null, 188).accountId

        val userSecurity = UserSecurity(
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
        Log.d("REGISTRATION", "password: ${String(pass)}")
        val encodedPassSalt = Base64.toBase64String(userSecurity.passwordKdfSalt)
        Log.d("REGISTRATION", "kdf salt: $encodedPassSalt length: ${encodedPassSalt.length}")
        Log.d("REGISTRATION", "kdf password: ${Base64.toBase64String(derivedPassword)}")
        Log.d("REGISTRATION", "master key: ${Base64.toBase64String(masterKey)}")
        val encryptedMasterKeyEncoded = Base64.toBase64String(userSecurity.encryptedMasterKey)
        Log.d("REGISTRATION", "encrypted master key: $encryptedMasterKeyEncoded length: ${encryptedMasterKeyEncoded.length}")
        val masterKeyIvEncoded = Base64.toBase64String(userSecurity.masterKeyEncryptionIv)
        Log.d("REGISTRATION", "master key iv: $masterKeyIvEncoded length: ${masterKeyIvEncoded.length}")
        Log.d("REGISTRATION", "mnemonic: ${String(mnemonic)}")
        val encryptedMnemonicEncoded = Base64.toBase64String(userSecurity.encryptedMnemonic)
        Log.d("REGISTRATION", "encrypted mnemonic: $encryptedMnemonicEncoded length: ${encryptedMnemonicEncoded.length}")
        val mnemonicIvEncoded = Base64.toBase64String(userSecurity.mnemonicEncryptionIv)
        Log.d("REGISTRATION", "mnemonic iv: $mnemonicIvEncoded length: ${mnemonicIvEncoded.length}")
    }

    fun confirmTfaRegistration(tfaCode: String) = userRepo.confirmTfaRegistration(tfaCode)

    fun provideSalutations() = userRepo.getSalutations()

    fun provideCountries() = userRepo.getCountries()

    fun loginWithTfa(email: CharSequence, password: CharSequence, tfaCode: CharSequence): Single<DashboardStatus> {
        return userRepo.loginWithTfaStep1(email.toString(), tfaCode.toString())
                .flatMap {
                    val publicKeyIndex188 = validateUserSecurity(password.toCharArray(), it)
                            ?: throw SgError(R.string.login_password_wrong)
                    it.publicKeyIndex188 = publicKeyIndex188
                    userRepo.loginWithTfaStep2(it)
                }
    }

    /**
     * @return public key index 188 if valid, null otherwise
     */
    private fun validateUserSecurity(password: CharArray, userSecurity: UserSecurity): String? {

        // cristi.paval, 4/27/18 - generate 256 bit password
        val derivedPassword = Cryptor.deriveKeyPbkdf2(userSecurity.passwordKdfSalt, password)


        // cristi.paval, 4/27/18 - decrypt master key
        val masterKey = Cryptor.decryptValue(derivedPassword, userSecurity.encryptedMasterKey, userSecurity.masterKeyEncryptionIv)

        // cristi.paval, 4/27/18 - decrypt mnemonic
        val paddedMnemonic = Cryptor.decryptValue(masterKey, userSecurity.encryptedMnemonic, userSecurity.mnemonicEncryptionIv)
        val mnemonic = String(Cryptor.removePadding(paddedMnemonic), charset("UTF-8"))

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

    companion object {
        const val TAG = "AuthUseCases"
    }
}