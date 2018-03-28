package com.soneso.stellargate.di

import android.content.Context
import android.text.TextUtils
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.soneso.stellargate.BuildConfig
import com.soneso.stellargate.domain.usecases.AccountManager
import com.soneso.stellargate.domain.usecases.AccountUseCases
import com.soneso.stellargate.domain.usecases.AuthManager
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.presentation.accounts.AccountsViewModel
import com.soneso.stellargate.presentation.auth.RegistrationViewModel
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
    fun provideAppPrefs() = SgPrefs(context)

    @Singleton
    @Provides
    fun provideAccountRepository(prefs: SgPrefs): com.soneso.stellargate.model.account.AccountRepository = com.soneso.stellargate.model.account.AccountSyncer(prefs)

    @Singleton
    @Provides
    fun provideAccountUseCases(repo: com.soneso.stellargate.model.account.AccountRepository): AccountUseCases = AccountManager(repo)

    @Provides
    fun provideAccountsViewModel(useCases: AccountUseCases) = AccountsViewModel(useCases)

    @Provides
    fun provideHomeViewModel(rm: com.soneso.stellargate.model.account.AccountRepository) = HomeViewModel(rm)

    @Provides
    @Singleton
    fun provideUserRepository(r: Retrofit): com.soneso.stellargate.model.user.UserRepository = com.soneso.stellargate.model.user.UserSyncer(r.create(com.soneso.stellargate.model.user.UserApi::class.java))

    @Provides
    @Singleton
    fun provideAuthUseCases(ur: com.soneso.stellargate.model.user.UserRepository): AuthUseCases = AuthManager(ur)

    @Provides
    fun provideRegistrationViewModel(useCases: AuthUseCases) = RegistrationViewModel(useCases)

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
                .baseUrl("http://horizon-testnet.stellargate.net/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(okHttpBuilder.build())
                .build()!!
    }
}