package com.soneso.stellargate.domain.usecases

import android.arch.lifecycle.Transformations
import com.soneso.stellargate.domain.data.Account
import com.soneso.stellargate.domain.util.Cryptor
import com.soneso.stellargate.model.dto.DataProvider
import com.soneso.stellargate.model.dto.DataStatus
import com.soneso.stellargate.model.user.UserRepository
import com.soneso.stellarmnemonics.Wallet
import com.soneso.stellarmnemonics.util.PrimitiveUtil

/**
 * Manager.
 * Created by cristi.paval on 3/22/18.
 */
class AuthManager(private val userRepo: UserRepository) : AuthUseCases {

    override fun generateAccount(email: CharSequence, password: CharSequence): DataProvider<Account> {

        val pass = CharArray(password.length)
        password.asSequence().forEachIndexed { index, c ->
            pass[index] = c
        }
        val account = createAccountForPass(pass)
        account.email = email.toString()

        val response = userRepo.createUserAccount(account)

        val dataProvider = DataProvider<Account>()
        dataProvider.liveStatus = Transformations.map(response.liveStatus, {
            when (it) {
                DataStatus.ERROR -> {
                    dataProvider.errorMessage = response.errorMessage
                }
                DataStatus.SUCCESS -> {
                    dataProvider.data = account
                }
                else -> {
                }
            }
            it
        })

        return dataProvider
    }

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

    companion object {
        const val TAG = "AuthManager"
    }
}