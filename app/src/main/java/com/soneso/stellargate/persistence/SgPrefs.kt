package com.soneso.stellargate.persistence

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.soneso.stellargate.SgApp
import java.util.*

/**
 * Shared Prefs.
 * Created by cristi.paval on 3/12/18.
 */
object SgPrefs {

    const val TAG = "SgPrefs"
    private const val PREF_NAME = "secured-app-prefs"
    private const val KEY_APP_ID = "app-id"
    private const val KEY_ACCOUNT_ID = "account-id"
    private const val KEY_SECRET_SEED = "secret-seed"
    private const val KEY_USERNAME = "username"
    private const val KEY_API_TOKEN = "api-token"

    private val prefs: SharedPreferences
    private val cipher: SgCipher

    init {
        val context = SgApp.sAppContext
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        cipher = SgCipher(context, PREF_NAME)
    }

    var username: String
        get() = getString(KEY_USERNAME)
        set(value) {
            saveString(KEY_USERNAME, value)
        }

    var apiToken: String
        get() = getString(KEY_API_TOKEN)
        set(value) = saveString(KEY_API_TOKEN, value)

    val appId: String
        get() {
            return if (!prefs.contains(KEY_APP_ID)) {
                val appId = UUID.randomUUID().toString()
                Log.d(TAG, "Generated appId: $appId")
                saveString(KEY_APP_ID, appId)
                appId
            } else {
                getString(KEY_APP_ID)
            }
        }

    fun saveAccount(accountId: String, secretSeed: String) {
        saveString(KEY_ACCOUNT_ID, accountId)
        saveString(KEY_SECRET_SEED, secretSeed)
    }

    private fun saveString(key: String, value: String) {
        val encryptedValue = cipher.encryptText(value) ?: value
        prefs.edit()
                .putString(key, encryptedValue)
                .apply()
    }

    private fun getString(key: String): String {
        val encrytedValue = prefs.getString(key, "")
        return cipher.decryptText(encrytedValue) ?: ""
    }

    fun accountId() = getString(KEY_ACCOUNT_ID)

    fun secretSeed() = getString(KEY_SECRET_SEED)
}