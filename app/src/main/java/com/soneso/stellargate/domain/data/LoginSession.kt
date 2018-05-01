package com.soneso.stellargate.domain.data

data class LoginSession(
        var username: String,
        var tfaSecret: String,
        var jwtToken: String
)