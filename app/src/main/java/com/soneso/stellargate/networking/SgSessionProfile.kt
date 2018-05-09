package com.soneso.stellargate.networking

import com.soneso.stellargate.persistence.SgPrefs

object SgSessionProfile {

    const val TAG = "SgSessionProfile"

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