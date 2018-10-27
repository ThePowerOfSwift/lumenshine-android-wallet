package com.soneso.lumenshine.networking

import com.soneso.lumenshine.persistence.LsPrefs

/**
 * Object holding key data needed for getting the user data from server. Some data such as password are kept in RAM memory.
 */
object LsSessionProfile {

    const val TAG = "LsSessionProfile"

    var jwtToken: String = LsPrefs.jwtToken
        private set

    const val langKey: String = "EN"

    init {
        LsPrefs.registerListener { key ->
            when (key) {
                LsPrefs.KEY_JWT_TOKEN -> {
                    jwtToken = LsPrefs.jwtToken
                }
            }
        }
    }
}