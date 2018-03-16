package com.soneso.stellargate.di

import android.content.Context
import com.soneso.stellargate.networking.RequestManager
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.ui.accounts.AccountsViewModel
import com.soneso.stellargate.ui.home.HomeViewModel
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
    fun provideRequestManager(sgPrefs: SgPrefs) = RequestManager(sgPrefs)

    @Provides
    fun provideHomeViewModel(rm: RequestManager) = HomeViewModel(rm)

    @Provides
    fun provideAccountsViewModel(rm: RequestManager) = AccountsViewModel(rm)
}