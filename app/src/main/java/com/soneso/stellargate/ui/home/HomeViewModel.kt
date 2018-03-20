package com.soneso.stellargate.ui.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.model.StellarAccount
import com.soneso.stellargate.networking.RequestManager

/**
 * View model.
 * Created by cristi.paval on 3/12/18.
 */
class HomeViewModel(private val requestManager: RequestManager) : ViewModel() {

    val liveAccountDetails = MutableLiveData<StellarAccount>()

    init {
        requestManager.getAccountDetails(liveAccountDetails)
    }
}