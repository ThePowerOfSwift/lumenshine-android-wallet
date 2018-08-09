package com.soneso.lumenshine.di

import android.arch.persistence.room.Room
import android.content.Context
import android.text.SpannableStringBuilder
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.model.UserRepository
import com.soneso.lumenshine.networking.NetworkUtil
import com.soneso.lumenshine.networking.api.SgApi
import com.soneso.lumenshine.networking.api.UserApi
import com.soneso.lumenshine.networking.dto.Parse
import com.soneso.lumenshine.networking.requester.UserRequester
import com.soneso.lumenshine.persistence.DbNames
import com.soneso.lumenshine.persistence.SgDatabase
import com.soneso.lumenshine.persistence.SgPrefs
import com.soneso.lumenshine.presentation.general.SgViewModelFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

/**
 * Dagger module.
 * Created by cristi.paval on 3/9/18.
 */
@Module
class AppModule(private val context: Context) {

    @Provides
    fun provideAuthRequester(r: Retrofit) = UserRequester(r.create(UserApi::class.java))

    @Provides
    @Singleton
    fun provideUserRepository(r: UserRequester) = UserRepository(r)

    @Provides
    @Singleton
    fun provideAuthUseCases(ur: UserRepository) = UserUseCases(ur)

    @Provides
    @Singleton
    fun provideSgViewModelFactory(userUC: UserUseCases) = SgViewModelFactory(userUC)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {

        // cristi.paval, 3/28/18 - retrofit builder
        return Retrofit.Builder()
                .baseUrl(SgApi.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(Parse.createMapper()))
                .client(NetworkUtil.sgHttpClient())
                .build()!!
    }

    @Provides
    @Singleton
    fun provideDatabase(): SgDatabase {

        val factory = SafeHelperFactory.fromUser(SpannableStringBuilder(SgPrefs.appPass))

        return Room.databaseBuilder(context, SgDatabase::class.java, DbNames.DB_NAME)
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
    }
}