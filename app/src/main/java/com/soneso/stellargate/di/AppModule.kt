package com.soneso.stellargate.di

import android.arch.persistence.room.Room
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.domain.usecases.AccountManager
import com.soneso.stellargate.domain.usecases.AccountUseCases
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.model.account.AccountRepository
import com.soneso.stellargate.model.account.AccountSyncer
import com.soneso.stellargate.model.user.UserApi
import com.soneso.stellargate.model.user.UserRepository
import com.soneso.stellargate.networking.UserRequester
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
    fun provideAccountUseCases(repo: com.soneso.stellargate.model.account.AccountRepository): AccountUseCases = AccountManager(repo)

    @Provides
    fun provideAccountsViewModel(useCases: AccountUseCases) = AccountsViewModel(useCases)

    @Provides
    fun provideHomeViewModel(rm: AccountRepository) = HomeViewModel(rm)

    @Provides
    fun provideUserRequester(r: Retrofit) = UserRequester(r.create(UserApi::class.java))

    @Provides
    @Singleton
    fun provideUserRepository(r: UserRequester, d: SgDatabase): UserRepository = UserRepository(r, d.userDao())

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
            if (TextUtils.isEmpty(request.header("Content-Type"))) {
                requestBuilder.addHeader("Content-Type", "application/json")
            }
            chain.proceed(requestBuilder.build())
        }

        // cristi.paval, 3/28/18 - jackson object mapper
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)

        // cristi.paval, 3/28/18 - retrofit builder
        return Retrofit.Builder()
//                .baseUrl("http://horizon-testnet.stellargate.net/")
                .baseUrl("https://7a61f89a-cb12-4613-b7d8-dfd5553f3210.mock.pstmn.io/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(okHttpBuilder.build())
                .build()!!
    }

    @Provides
    @Singleton
    fun provideDatabase(): SgDatabase {

        val factory = SafeHelperFactory.fromUser(SpannableStringBuilder(SgPrefs.appId()))

        return Room.databaseBuilder(context, SgDatabase::class.java, SgDatabase.DB_NAME)
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
    }
}