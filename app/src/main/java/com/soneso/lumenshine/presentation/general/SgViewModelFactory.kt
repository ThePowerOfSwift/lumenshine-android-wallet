package com.soneso.lumenshine.presentation.general

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.presentation.auth.AuthViewModel
import com.soneso.lumenshine.presentation.settings.SettingsViewModel

class SgViewModelFactory(
        private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(userUseCases) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(userUseCases) as T
            else -> throw IllegalArgumentException("View Model not found here not found")
        }
    }
}