package com.soneso.stellargate.model.user

import com.soneso.stellargate.domain.data.User
import com.soneso.stellargate.model.dto.DataProvider

/**
 * User.
 * Created by cristi.paval on 3/26/18.
 */
interface UserRepository {

    fun createUserAccount(user: User): DataProvider<User>
}