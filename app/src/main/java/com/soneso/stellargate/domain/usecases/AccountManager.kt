package com.soneso.stellargate.domain.usecases

/**
 * Manager.
 * Created by cristi.paval on 3/20/18.
 */
class AccountManager(private val repo: com.soneso.stellargate.model.account.AccountRepository) : AccountUseCases {

    override fun createAccount(accountId: String) {
        repo.createUserAccount(accountId)
    }
}