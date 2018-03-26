package com.soneso.stellargate.model

import com.soneso.stellargate.domain.User

/**
 * User.
 * Created by cristi.paval on 3/26/18.
 */
interface UserRepository {

    fun createUserAccount(user: User)
}