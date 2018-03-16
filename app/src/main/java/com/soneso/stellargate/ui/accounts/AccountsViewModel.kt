package com.soneso.stellargate.ui.accounts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.networking.RequestManager

/**
 * View model.
 * Created by cristi.paval on 3/13/18.
 */
class AccountsViewModel(private val requestManager: RequestManager) : ViewModel() {

    val liveAccountDetails = MutableLiveData<String>()

    init {
        requestManager.getAccountDetails(liveAccountDetails)
    }
}