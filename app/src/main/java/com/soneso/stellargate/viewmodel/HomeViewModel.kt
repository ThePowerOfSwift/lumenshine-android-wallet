package com.soneso.stellargate.viewmodel

import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.networking.RequestManager

/**
 * View model.
 * Created by cristi.paval on 3/12/18.
 */
class HomeViewModel : ViewModel() {

    private val requestManager = RequestManager()
}