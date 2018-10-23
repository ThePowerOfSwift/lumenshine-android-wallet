package com.soneso.lumenshine.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soneso.lumenshine.domain.data.Country
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.model.entities.RegistrationStatus
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.util.putValue
import com.soneso.lumenshine.util.LsException
import com.soneso.lumenshine.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class AuthViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val liveLastUsername: LiveData<String> = MutableLiveData()

    @Suppress("MemberVisibilityCanBePrivate")
    val liveSalutations: LiveData<Resource<List<String>, LsException>> = MutableLiveData()

    val liveTfaSecret: LiveData<Resource<String, ServerException>> = MutableLiveData()

    val liveTfaConfirmation: LiveData<Resource<Boolean, ServerException>> = MutableLiveData()

    val liveTfaChangeConfirmation: LiveData<Resource<Boolean, ServerException>> = MutableLiveData()

    val liveMnemonicConfirmation: LiveData<Resource<Boolean, LsException>> = MutableLiveData()

    @Suppress("MemberVisibilityCanBePrivate")
    val liveCountries: LiveData<Resource<List<Country>, LsException>> = MutableLiveData()

    val liveMnemonic: LiveData<Resource<String, LsException>> = MutableLiveData()

    val liveConfirmationMail: LiveData<Resource<Boolean, LsException>> = MutableLiveData()

    val liveCredentialResetEmail: LiveData<Resource<Boolean, LsException>> = MutableLiveData()

    val liveRegistrationStatus: LiveData<RegistrationStatus?> = MutableLiveData()

    val liveRegistrationRefresh: LiveData<Resource<Boolean, ServerException>> = MutableLiveData()

    val liveRegistration: LiveData<Resource<Boolean, ServerException>> = MutableLiveData()

    val liveLogin: LiveData<Resource<Boolean, ServerException>> = MutableLiveData()

    val liveLogout: LiveData<Unit> = MutableLiveData()

    var isFingerprintFlow = false

    private val compositeDisposable = CompositeDisposable()

    init {
        initLastUsername()
        initRegistrationStatus()
    }

    private fun initRegistrationStatus() {

        val d = userUseCases.provideRegistrationStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Timber.d("Registration status just published.")
                    liveRegistrationStatus.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun createAccount(email: CharSequence, password: CharSequence, countryPosition: Int = 0) {

        val country = try {
            liveCountries.value?.success()?.get(countryPosition)
        } catch (ignored: Exception) {
            null
        }

        val d = userUseCases.registerAccount(email, password, country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveRegistration.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun confirmTfaRegistration(tfaCode: CharSequence) {

        val d = userUseCases.confirmTfaRegistration(tfaCode.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveTfaConfirmation.putValue(it)
                }
        compositeDisposable.add(d)
    }

    @Suppress("unused")
    fun refreshSalutations() {

        val d = userUseCases.provideSalutations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveSalutations.putValue(it)
                }
        compositeDisposable.add(d)
    }

    @Suppress("unused")
    fun refreshCountries() {

        val d = userUseCases.provideCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveCountries.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun login(email: CharSequence, password: CharSequence, tfa: CharSequence? = null) {

        val d = userUseCases.login(email, password, tfa)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveLogin.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun fetchMnemonic() {

        val d = userUseCases.provideMnemonicForCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveMnemonic.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun confirmMnemonic() {

        val d = userUseCases.confirmMnemonic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveMnemonicConfirmation.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun resendConfirmationMail() {

        val d = userUseCases.resendConfirmationMail()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveConfirmationMail.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun refreshRegistrationStatus() {

        val d = userUseCases.refreshRegistrationStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveRegistrationRefresh.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun fetchTfaSecret() {

        val d = userUseCases.provideTfaSecret()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveTfaSecret.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun requestPasswordResetEmail(email: CharSequence) {

        val d = userUseCases.requestPasswordReset(email.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveCredentialResetEmail.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun requestTfaResetEmail(email: CharSequence) {

        val d = userUseCases.requestTfaReset(email.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveCredentialResetEmail.putValue(it)
                }
        compositeDisposable.add(d)
    }

    private fun initLastUsername() {

        val d = userUseCases.provideLastUsername()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: String ->
                    liveLastUsername.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun confirmTfaSecretChange(tfaCode: CharSequence) {

        val d = userUseCases.confirmTfaSecretChange(tfaCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveTfaChangeConfirmation.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun logout() {
        val d = userUseCases.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveLogout.putValue(Unit)
                }
        compositeDisposable.add(d)
    }

    override fun onCleared() {

        compositeDisposable.dispose()
        super.onCleared()
    }
}