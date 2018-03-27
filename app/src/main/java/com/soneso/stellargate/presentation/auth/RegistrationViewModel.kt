package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.domain.usecases.AuthUseCases

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class RegistrationViewModel(private val authUseCases: AuthUseCases) : ViewModel() {

    fun createAccount(email: CharSequence, password: CharSequence) =
            authUseCases.createAccount(email, password)
}