package com.soneso.lumenshine.model.wrapper

import com.soneso.lumenshine.model.entities.RegistrationStatus
import com.soneso.lumenshine.networking.dto.auth.ConfirmTfaResponse
import com.soneso.lumenshine.networking.dto.auth.ConfirmTfaSecretChangeResponse
import com.soneso.lumenshine.networking.dto.auth.GetRegistrationStatusResponse
import com.soneso.lumenshine.networking.dto.auth.LoginStep2Response

fun GetRegistrationStatusResponse.toRegistrationStatus(username: String): RegistrationStatus {

    return RegistrationStatus(username, mailConfirmed, tfaConfirmed, mnemonicConfirmed)
}

fun ConfirmTfaResponse.toRegistrationStatus(username: String): RegistrationStatus {

    return RegistrationStatus(username, emailConfirmed, tfaConfirmed, mnemonicConfirmed)
}

fun ConfirmTfaSecretChangeResponse.toRegistrationStatus(username: String): RegistrationStatus {

    return RegistrationStatus(username, mailConfirmed, tfaConfirmed, mnemonicConfirmed)
}

fun LoginStep2Response.toRegistrationStatus(username: String): RegistrationStatus {

    return RegistrationStatus(username, emailConfirmed, tfaConfirmed, mnemonicConfirmed)
}