package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.domain.usecases.AuthUseCases

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class RegistrationViewModel(private val authUseCases: AuthUseCases) : ViewModel() {

    val liveMnemonic: MutableLiveData<String> = MutableLiveData()

    fun createAccount(email: CharSequence, password: CharSequence) = authUseCases.generateAccount(email, password)
}