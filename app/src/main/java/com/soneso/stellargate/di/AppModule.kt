package com.soneso.stellargate.di

import android.content.Context
import com.soneso.stellargate.domain.usecases.AccountManager
import com.soneso.stellargate.domain.usecases.AccountUseCases
import com.soneso.stellargate.domain.usecases.AuthManager
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.model.*
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.presentation.accounts.AccountsViewModel
import com.soneso.stellargate.presentation.auth.RegistrationViewModel
import com.soneso.stellargate.presentation.home.HomeViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
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
    fun provideAccountsViewModel(useCases: AccountUseCases) = AccountsViewModel(useCases)

    @Provides
    fun provideHomeViewModel(rm: AccountRepository) = HomeViewModel(rm)

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository = UserSyncer(userApi)

    @Provides
    @Singleton
    fun provideAuthUseCases(ur: UserRepository): AuthUseCases = AuthManager(ur)

    @Provides
    fun provideRegistrationViewModel(useCases: AuthUseCases) = RegistrationViewModel(useCases)

    @Provides
    @Singleton
    fun provideRetrofit() = Retrofit.Builder()
            .baseUrl("http://horizon-testnet.stellargate.net/")
            .build()!!

    @Provides
    fun provideUserApi(r: Retrofit): UserApi = r.create(UserApi::class.java)
}