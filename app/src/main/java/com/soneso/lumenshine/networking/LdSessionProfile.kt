package com.soneso.lumenshine.networking

import com.soneso.lumenshine.persistence.SgPrefs

/**
 * Object holding key data needed for getting the user data from server. Some data such as password are kept in RAM memory.
 */
object LdSessionProfile {

    const val TAG = "LdSessionProfile"

    var password: CharSequence = ""

    var username: String = SgPrefs.username
        private set

    var jwtToken: String = SgPrefs.jwtToken
        private set

    var tfaSecret: String = SgPrefs.tfaSecret
        private set

    const val langKey: String = "EN"

    init {
        SgPrefs.registerListener { key ->

            when (key) {
                SgPrefs.KEY_USERNAME -> {
                    username = SgPrefs.username
                }
                SgPrefs.KEY_JWT_TOKEN -> {
                    jwtToken = SgPrefs.jwtToken
                }
                SgPrefs.KEY_TFA_SECRET -> {
                    tfaSecret = SgPrefs.tfaSecret
                }
            }
        }
    }
}