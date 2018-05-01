package com.soneso.stellargate.di

import android.arch.persistence.room.Room
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.domain.usecases.AccountManager
import com.soneso.stellargate.domain.usecases.AccountUseCases
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.model.UserRepository
import com.soneso.stellargate.model.account.AccountRepository
import com.soneso.stellargate.model.account.AccountSyncer
import com.soneso.stellargate.networking.SessionProfileService
import com.soneso.stellargate.networking.api.AuthApi
import com.soneso.stellargate.networking.api.SgApi
import com.soneso.stellargate.networking.dto.Parse
import com.soneso.stellargate.networking.requester.AuthRequester
import com.soneso.stellargate.persistence.DbNames
import com.soneso.stellargate.persistence.SgDatabase
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.presentation.accounts.AccountsViewModel
import com.soneso.stellargate.presentation.general.SgViewModelFactory
import com.soneso.stellargate.presentation.home.HomeViewModel
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger module.
 * Created by cristi.paval on 3/9/18.
 */
@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideAccountRepository(): AccountRepository = AccountSyncer()

    @Singleton
    @Provides
    fun provideAccountUseCases(repo: AccountRepository): AccountUseCases = AccountManager(repo)

    @Provides
    fun provideAccountsViewModel(useCases: AccountUseCases) = AccountsViewModel(useCases)

    @Provides
    fun provideHomeViewModel(rm: AccountRepository) = HomeViewModel(rm)

    @Provides
    @Singleton
    fun provideSessionProfileService() = SessionProfileService()

    @Provides
    fun provideAuthRequester(r: Retrofit, s: SessionProfileService) = AuthRequester(r.create(AuthApi::class.java), s)

    @Provides
    @Singleton
    fun provideUserRepository(r: AuthRequester, d: SgDatabase) = UserRepository(r, d.userDao())

    @Provides
    @Singleton
    fun provideAuthUseCases(ur: UserRepository) = AuthUseCases(ur)

    @Provides
    @Singleton
    fun provideSgViewModelFactory(useCases: AuthUseCases) = SgViewModelFactory(useCases)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {

        // cristi.paval, 3/28/18 - okhttp builder
        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(interceptor)
        }
        okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.addInterceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            if (TextUtils.isEmpty(request.header(SgApi.HEADER_NAME_CONTENT_TYPE))) {
                requestBuilder.addHeader(SgApi.HEADER_NAME_CONTENT_TYPE, SgApi.HEADER_VALUE_CONTENT_TYPE)
            }
            chain.proceed(requestBuilder.build())
        }


        // cristi.paval, 3/28/18 - retrofit builder
        return Retrofit.Builder()
                .baseUrl(SgApi.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(Parse.createMapper()))
                .client(okHttpBuilder.build())
                .build()!!
    }

    @Provides
    @Singleton
    fun provideDatabase(): SgDatabase {

        val factory = SafeHelperFactory.fromUser(SpannableStringBuilder(SgPrefs.appId))

        return Room.databaseBuilder(context, SgDatabase::class.java, DbNames.DB_NAME)
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
    }
}