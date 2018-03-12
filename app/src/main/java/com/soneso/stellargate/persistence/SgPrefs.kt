package com.soneso.stellargate.persistence

import android.content.Context
import android.util.Log
import org.stellar.sdk.KeyPair

/**
 * Shared Prefs.
 * Created by cristi.paval on 3/12/18.
 */
class SgPrefs(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    init {
        if (accountId().isEmpty()) {
            Log.i(TAG, "Generating account...")
            val pair = KeyPair.random()
            saveAccount(pair.accountId, String(pair.secretSeed))
            Log.i(TAG, "Account saved: ${accountId()}  ---  ${secretSeed()}")
        }
    }

    fun saveAccount(accountId: String, secretSeed: String) {
        prefs.edit()
                .putString(KEY_ACCOUNT_ID, accountId)
                .putString(KEY_SECRET_SEED, secretSeed)
                .apply()
    }

    fun accountId() = prefs.getString(KEY_ACCOUNT_ID, "")

    fun secretSeed() = prefs.getString(KEY_SECRET_SEED, "")

    companion object {
        const val TAG = "SgPrefs"
        private const val PREF_NAME = "app-prefs"
        private const val KEY_ACCOUNT_ID = "account-id"
        private const val KEY_SECRET_SEED = "secret-seed"
    }
}