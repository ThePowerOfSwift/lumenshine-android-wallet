package com.soneso.stellargate.di

import com.soneso.stellargate.presentation.accounts.AccountsFragment
import com.soneso.stellargate.presentation.home.HomeFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component.
 * Created by cristi.paval on 3/9/18.
 */
@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {

    fun inject(fragment: HomeFragment)

    fun inject(fragment: AccountsFragment)
}