package com.soneso.stellargate.domain.usecases

/**
 * Class which provides data to ui in a convenient manner. Gets objects from repository and wraps them here for presentation layer.
 * Created by cristi.paval on 3/22/18.
 */
interface AuthUseCases {

    fun createAccount(email: CharSequence, password: CharSequence)
}