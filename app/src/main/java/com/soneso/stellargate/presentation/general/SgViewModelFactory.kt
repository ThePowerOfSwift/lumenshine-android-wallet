package com.soneso.stellargate.presentation.general

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.soneso.stellargate.domain.usecases.AccountUseCases
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.presentation.auth.AuthViewModel
import com.soneso.stellargate.presentation.home.HomeViewModel

class SgViewModelFactory(
        private val authUseCases: AuthUseCases,
        private val accountUseCases: AccountUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(authUseCases) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(accountUseCases) as T
            else -> throw IllegalArgumentException("View Model not found here not found")
        }
    }
}