package com.soneso.stellargate.domain.usecases

import android.util.Log
import com.soneso.stellargate.domain.util.CryptoUtil
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.util.PrimitiveUtil

/**
 * Manager.
 * Created by cristi.paval on 3/22/18.
 */
class AuthManager : AuthUseCases {

    override fun createAccount(email: CharSequence, password: CharSequence) {
        val passwordSalt = CryptoUtil.generateSalt()
        val masterKeyIV = CryptoUtil.generateIv()
        val masterKey = CryptoUtil.generateMasterKey()
        val mnemonicIV = CryptoUtil.generateIv()

        val mnemonic = Wallet.generate24WordMnemonic()

        val mnemonic16bytes = CryptoUtil.padCharsTo16BytesFormat(mnemonic)

        val pass = CharArray(password.length)
        password.asSequence().forEachIndexed { index, c ->
            pass[index] = c
        }
        val derivedPassword = CryptoUtil.deriveKeyPbkdf2(pass, passwordSalt)

        val encryptedMasterKey = CryptoUtil.encryptValue(masterKey, derivedPassword, masterKeyIV)
        val encryptedMnemonic = CryptoUtil.encryptValue(PrimitiveUtil.toBytes(mnemonic16bytes), masterKey, mnemonicIV)

        Log.d(TAG, "User security data was successfully created!")
    }

    companion object {
        const val TAG = "AuthManager"
    }
}