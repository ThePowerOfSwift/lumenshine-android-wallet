package com.soneso.stellargate.presentation.general

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.presentation.auth.RegistrationViewModel

class SgViewModelFactory(
        private val authUseCases: AuthUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> RegistrationViewModel(authUseCases) as T
            else -> throw IllegalArgumentException("View Model not found here not found")
        }
    }
}