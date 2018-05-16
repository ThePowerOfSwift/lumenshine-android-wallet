package com.soneso.stellargate.presentation.general

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.soneso.stellargate.domain.usecases.UserUseCases
import com.soneso.stellargate.presentation.auth.AuthViewModel

class SgViewModelFactory(
        private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(userUseCases) as T
            else -> throw IllegalArgumentException("View Model not found here not found")
        }
    }
}