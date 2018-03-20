package com.soneso.stellargate.di

import android.content.Context
import com.soneso.stellargate.domain.usecases.AccountManager
import com.soneso.stellargate.domain.usecases.AccountUseCases
import com.soneso.stellargate.model.AccountRepository
import com.soneso.stellargate.model.AccountSyncer
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.presentation.accounts.AccountsViewModel
import com.soneso.stellargate.presentation.home.HomeViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module.
 * Created by cristi.paval on 3/9/18.
 */
@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideAppPrefs() = SgPrefs(context)

    @Singleton
    @Provides
    fun provideAccountRepository(prefs: SgPrefs): AccountRepository = AccountSyncer(prefs)

    @Singleton
    @Provides
    fun provideAccountUseCases(repo: AccountRepository): AccountUseCases = AccountManager(repo)

    @Provides
    fun provideHomeViewModel(rm: AccountRepository) = HomeViewModel(rm)

    @Provides
    fun provideAccountsViewModel(useCases: AccountUseCases) = AccountsViewModel(useCases)
}