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
    const val KEY_USERNAME = "username"
    const val KEY_JWT_TOKEN = "api-token"
    const val KEY_TFA_SECRET = "tfa-secret"

    val prefs: SharedPreferences
    private val cipher: SgCipher

    init {
        val context = SgApp.sAppContext
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        cipher = SgCipher(context, PREF_NAME)
    }

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

    var username: String
        get() = getString(KEY_USERNAME)
        set(value) {
            saveString(KEY_USERNAME, value)
        }

    var jwtToken: String
        get() = getString(KEY_JWT_TOKEN)
        set(value) = saveString(KEY_JWT_TOKEN, value)

    var tfaSecret: String
        get() = getString(KEY_TFA_SECRET)
        set(value) = saveString(KEY_TFA_SECRET, value)

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
}