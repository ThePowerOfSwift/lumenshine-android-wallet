package com.soneso.stellargate.domain.usecases

import com.soneso.stellargate.model.AccountRepository

/**
 * Manager.
 * Created by cristi.paval on 3/20/18.
 */
class AccountManager(private val repo: AccountRepository) : AccountUseCases {

    override fun createAccount(accountId: String) {
        repo.createAccount(accountId)
    }
}