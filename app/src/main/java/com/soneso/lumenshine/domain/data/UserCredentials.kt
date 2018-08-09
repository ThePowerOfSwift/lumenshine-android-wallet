package com.soneso.lumenshine.domain.data

class UserCredentials(
        val username: String,
        val tfaSecret: String,
        val pass: String = ""
)