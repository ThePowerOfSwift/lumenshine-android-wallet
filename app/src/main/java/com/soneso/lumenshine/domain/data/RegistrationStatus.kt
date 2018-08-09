package com.soneso.lumenshine.domain.data

class RegistrationStatus(
        val emailConfirmed: Boolean,
        val mnemonicConfirmed: Boolean,
        val tfaConfirmed: Boolean
)