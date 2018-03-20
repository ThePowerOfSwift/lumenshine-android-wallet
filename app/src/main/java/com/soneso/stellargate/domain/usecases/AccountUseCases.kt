package com.soneso.stellargate.domain.usecases

/**
 * Class which provides data to ui in a convenient manner. Gets objects from repository and wraps them here for presentation layer.
 * Created by cristi.paval on 3/20/18.
 */
interface AccountUseCases {

    fun createAccount(accountId: String)
}