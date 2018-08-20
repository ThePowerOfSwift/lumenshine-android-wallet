package com.soneso.lumenshine.presentation.general

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.soneso.lumenshine.di.AppComponent
import com.soneso.lumenshine.presentation.auth.AuthViewModel
import com.soneso.lumenshine.presentation.settings.SettingsViewModel
import com.soneso.lumenshine.presentation.wallets.WalletsViewModel

class SgViewModelFactory(
        private val appComponent: AppComponent
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(appComponent.userUseCases) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(appComponent.userUseCases) as T
            modelClass.isAssignableFrom(WalletsViewModel::class.java) -> WalletsViewModel(appComponent.walletsUseCase) as T
            else -> throw IllegalArgumentException("View Model not found here not found")
        }
    }
}