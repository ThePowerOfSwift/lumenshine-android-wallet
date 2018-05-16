package com.soneso.stellargate.domain.usecases

import com.soneso.stellargate.model.AccountRepository

/**
 * Manager.
 * Created by cristi.paval on 3/20/18.
 */
class AccountUseCases(private val repo: AccountRepository) {

    fun createAccount(accountId: String) {
        repo.createUserAccount(accountId)
    }
}