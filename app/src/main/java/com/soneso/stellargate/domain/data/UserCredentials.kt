package com.soneso.stellargate.domain.data

class UserCredentials(
        val username: String,
        val tfaSecret: String,
        val pass: String = ""
)