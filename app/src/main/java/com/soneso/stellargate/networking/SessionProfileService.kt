package com.soneso.stellargate.networking

import com.soneso.stellargate.persistence.SgPrefs

class SessionProfileService {

    private var apiToken = ""
    private var tfaCode = ""

    val langKey: String
        get() = "EN"

    var authToken: String
        set(value) {
            apiToken = value
            SgPrefs.apiToken = value
        }
        get() {
            if (apiToken.isEmpty()) {
                apiToken = SgPrefs.apiToken
            }
            return apiToken
        }

    var tfaSecret: String
        get() {
            if (tfaCode.isEmpty()) {
                tfaCode = SgPrefs.tfaSecret
            }
            return tfaCode
        }
        set(value) {
            tfaCode = value
            SgPrefs.tfaSecret = value
        }
}