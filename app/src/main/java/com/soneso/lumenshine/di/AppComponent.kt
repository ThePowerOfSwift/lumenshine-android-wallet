package com.soneso.lumenshine.di

import com.soneso.lumenshine.presentation.general.SgActivity
import com.soneso.lumenshine.presentation.general.SgFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component.
 * Created by cristi.paval on 3/9/18.
 */
@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {

    fun inject(fragment: SgFragment)

    fun inject(fragment: SgActivity)
}