package com.soneso.stellargate.networking

import com.soneso.stellargate.persistence.SgPrefs

class SessionProfileService {

    private var apiToken = ""

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
}