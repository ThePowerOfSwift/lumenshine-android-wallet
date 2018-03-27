package com.soneso.stellargate.model

import com.soneso.stellargate.domain.User

/**
 * Class used to user operations to server.
 * Created by cristi.paval on 3/26/18.
 */
class UserSyncer() : UserRepository {

    override fun createUserAccount(user: User) {
        val registrationRequest = RegistrationRequest(
                user.email,
                user.securityData.publicKeyIndex0,
                user.securityData.publicKeyIndex188,
                user.securityData.derivedPassword,
                user.securityData.encryptedMasterKey,
                user.securityData.masterKeyIV,
                user.securityData.encryptedMnemonic,
                user.securityData.mnemonicIV
        )
//        userApi.registerUser(registrationRequest)
    }
}