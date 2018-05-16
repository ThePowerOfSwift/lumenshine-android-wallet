package com.soneso.stellargate.presentation.accounts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.domain.data.StellarAccount
import com.soneso.stellargate.domain.usecases.AccountUseCases
import org.stellar.sdk.KeyPair

/**
 * View model.
 * Created by cristi.paval on 3/13/18.
 */
class AccountsViewModel(private val useCases: AccountUseCases) : ViewModel() {

    val liveAccountDetails = MutableLiveData<StellarAccount>()

    fun createAccount() {
        val keyPair = KeyPair.random()
        useCases.createAccount(keyPair.accountId)
    }
}