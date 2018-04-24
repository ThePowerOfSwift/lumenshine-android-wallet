package com.soneso.stellargate.domain.usecases

import android.util.Log
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.domain.data.UserLogin
import com.soneso.stellargate.domain.util.Cryptor
import com.soneso.stellargate.model.dto.DataProvider
import com.soneso.stellargate.model.user.UserRepository
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.util.PrimitiveUtil
import org.bouncycastle.util.encoders.Base64

/**
 * Manager.
 * Created by cristi.paval on 3/22/18.
 */
class AuthUseCases(private val userRepo: UserRepository) {

    fun generateAccount(email: CharSequence, password: CharSequence): DataProvider<UserLogin> {

        val pass = CharArray(password.length)
        password.asSequence().forEachIndexed { index, c ->
            pass[index] = c
        }
        val account = createAccountForPass(pass)
        account.email = email.toString()

        return userRepo.createUserAccount(account)
    }

    //password, kdf salt, kdf password, master key, master key iv, encrypted master key, nmemonic, mnemonic iv, encrypted mnemonic
    private fun createAccountForPass(pass: CharArray): Account {

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

        if (BuildConfig.DEBUG) {
            logSecurityData(
                    pass,
                    passwordSalt,
                    derivedPassword,
                    masterKey,
                    masterKeyIv,
                    encryptedMasterKey,
                    mnemonic,
                    mnemonicIv,
                    encryptedMnemonic
            )
        }

        return Account(
                publicKeyIndex0,
                publicKeyIndex188,
                passwordSalt,
                encryptedMasterKey,
                masterKeyIv,
                encryptedMnemonic,
                mnemonicIv
        )
    }

    private fun logSecurityData(
            pass: CharArray,
            passwordSalt: ByteArray,
            derivedPassword: ByteArray,
            masterKey: ByteArray,
            masterKeyIv: ByteArray,
            encryptedMasterKey: ByteArray,
            mnemonic: CharArray,
            mnemonicIv: ByteArray,
            encryptedMnemonic: ByteArray
    ) {
        Log.d("REGISTRATION", "password: ${String(pass)}")
        val encodedPassSalt = Base64.toBase64String(passwordSalt)
        Log.d("REGISTRATION", "kdf salt: $encodedPassSalt length: ${encodedPassSalt.length}")
        Log.d("REGISTRATION", "kdf password: ${Base64.toBase64String(derivedPassword)}")
        Log.d("REGISTRATION", "master key: ${Base64.toBase64String(masterKey)}")
        val encryptedMasterKeyEncoded = Base64.toBase64String(encryptedMasterKey)
        Log.d("REGISTRATION", "encrypted master key: $encryptedMasterKeyEncoded length: ${encryptedMasterKeyEncoded.length}")
        val masterKeyIvEncoded = Base64.toBase64String(masterKeyIv)
        Log.d("REGISTRATION", "master key iv: $masterKeyIvEncoded length: ${masterKeyIvEncoded.length}")
        Log.d("REGISTRATION", "mnemonic: ${String(mnemonic)}")
        val encryptedMnemonicEncoded = Base64.toBase64String(encryptedMnemonic)
        Log.d("REGISTRATION", "encrypted mnemonic: $encryptedMnemonicEncoded length: ${encryptedMnemonicEncoded.length}")
        val mnemonicIvEncoded = Base64.toBase64String(mnemonicIv)
        Log.d("REGISTRATION", "mnemonic iv: $mnemonicIvEncoded length: ${mnemonicIvEncoded.length}")
    }

    fun confirmTfaRegistration(tfaCode: String) = userRepo.confirmTfaRegistration(tfaCode)

    companion object {
        const val TAG = "AuthUseCases"
    }
}