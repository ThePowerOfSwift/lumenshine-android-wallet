package com.soneso.stellargate.presentation.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.domain.StellarAccount
import com.soneso.stellargate.model.AccountRepository

/**
 * View model.
 * Created by cristi.paval on 3/12/18.
 */
class HomeViewModel(private val accountSyncer: AccountRepository) : ViewModel() {

    val liveAccountDetails = MutableLiveData<StellarAccount>()

//    init {
//        accountSyncer.getAccountDetails(liveAccountDetails)
//    }
}