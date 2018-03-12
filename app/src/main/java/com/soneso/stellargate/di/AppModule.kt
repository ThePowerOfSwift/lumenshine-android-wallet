package com.soneso.stellargate.di

import android.content.Context
import com.soneso.stellargate.persistence.SgPrefs
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
}