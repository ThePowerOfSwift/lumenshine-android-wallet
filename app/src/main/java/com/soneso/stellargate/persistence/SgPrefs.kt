package com.soneso.stellargate.persistence

import android.content.Context
import android.util.Log
import com.soneso.stellargate.persistence.secureprefs.DeCryptor
import com.soneso.stellargate.persistence.secureprefs.EnCryptor
import org.stellar.sdk.KeyPair

/**
 * Shared Prefs.
 * Created by cristi.paval on 3/12/18.
 */
class SgPrefs(context: Context, private val alias: String, private val iv: ByteArray) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val enCryptor = EnCryptor()
    private val deCryptor = DeCryptor()

    init {
        if (accountId().isEmpty()) {
            Log.i(TAG, "Generating account...")
            val pair = KeyPair.random()
            saveAccount(pair.accountId, String(pair.secretSeed))
            Log.i(TAG, "Account saved: ${accountId()}  ---  ${secretSeed()}")
        }
    }

    fun saveAccount(accountId: String, secretSeed: String) {
        saveString(KEY_ACCOUNT_ID, accountId)
        saveString(KEY_SECRET_SEED, secretSeed)
    }

    private fun saveString(key: String, value: String) {
        val encryptedKey = enCryptor.encryptText(alias, iv, key).toString(charset("UTF-8"))
        val encryptedValue = enCryptor.encryptText(alias, iv, value).toString(charset("UTF-8"))
        prefs.edit()
                .putString(encryptedKey, encryptedValue)
                .apply()
    }

    private fun getString(key: String): String {
        val encryptedKey = enCryptor.encryptText(alias, iv, key).toString(charset("UTF-8"))
        val encrytedValue = prefs.getString(encryptedKey, "")
        return deCryptor.decryptData(alias, iv, encrytedValue)
    }

    fun accountId() = getString(KEY_ACCOUNT_ID)

    fun secretSeed() = getString(KEY_SECRET_SEED)

    companion object {
        const val TAG = "SgPrefs"
        private const val PREF_NAME = "app-prefs"
        private const val KEY_ACCOUNT_ID = "account-id"
        private const val KEY_SECRET_SEED = "secret-seed"
    }
}