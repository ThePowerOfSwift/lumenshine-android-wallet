package com.soneso.lumenshine.presentation.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.*
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class AuthViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val liveLastCredentials: LiveData<SgViewState<UserCredentials>> = MutableLiveData()

    val liveSalutations: LiveData<SgViewState<List<String>>> = MutableLiveData()

    val liveTfaSecret: LiveData<SgViewState<TfaSecret>> = MutableLiveData()

    val liveCountries: LiveData<SgViewState<List<Country>>> = MutableLiveData()

    val liveRegistrationStatus: LiveData<SgViewState<RegistrationStatus>> = MutableLiveData()

    val liveMnemonic: LiveData<SgViewState<String>> = MutableLiveData()

    val liveConfirmationMail: LiveData<SgViewState<Unit>> = MutableLiveData()

    val liveCredentialResetEmail: LiveData<SgViewState<Unit>> = MutableLiveData()

    fun createAccount(email: CharSequence, password: CharSequence, countryPosition: Int) {

        val country = try {
            liveCountries.value?.data?.get(countryPosition)
        } catch (ignored: Exception) {
            null
        }

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.registerAccount(email, password, country)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun confirmTfaRegistration(tfaCode: String) {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.confirmTfaRegistration(tfaCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun refreshSalutations() {

        (liveSalutations as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.provideSalutations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveSalutations.value = SgViewState(it)
                }, {
                    liveSalutations.value = SgViewState(it as SgError)
                })
    }

    fun refreshCountries() {

        (liveCountries as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.provideCountries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveCountries.value = SgViewState(it)
                }, {
                    liveCountries.value = SgViewState(it as SgError)
                })
    }

    fun login(email: CharSequence, password: CharSequence, tfaCode: CharSequence) {

        val tfa = if (tfaCode.isBlank()) {
            null
        } else {
            tfaCode
        }

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.login(email, password, tfa)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun loginAndFingerprintSetup(email: CharSequence, password: CharSequence, tfaCode: CharSequence) {

        val tfa = if (tfaCode.isBlank()) {
            null
        } else {
            tfaCode
        }

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.login(email, password, tfa)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.fingerprintSetupRequested = true
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun fetchMnemonic() {

        (liveMnemonic as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.provideMnemonicForCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveMnemonic.value = SgViewState(it)
                }, {
                    liveMnemonic.value = SgViewState(SgError(R.string.unknown_error))
                })
    }

    fun confirmMnemonic() {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.confirmMnemonic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun resendConfirmationMail() {

        (liveConfirmationMail as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.resendConfirmationMail()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveConfirmationMail.value = SgViewState(Unit)
                }, {
                    liveConfirmationMail.value = SgViewState(it as SgError)
                })
    }

    fun refreshRegistrationStatus() {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.provideRegistrationStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun fetchTfaSecret() {

        (liveTfaSecret as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.provideTfaSecret()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveTfaSecret.value = SgViewState(it)
                }, {
                    liveTfaSecret.value = SgViewState(it as SgError)
                })
    }

    fun requestPasswordResetEmail(email: CharSequence) {

        (liveCredentialResetEmail as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.requestPasswordReset(email.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveCredentialResetEmail.value = SgViewState(Unit)
                }, {
                    liveCredentialResetEmail.value = SgViewState(it as SgError)
                })
    }

    fun requestTfaResetEmail(email: CharSequence) {

        (liveCredentialResetEmail as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.requestTfaReset(email.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveCredentialResetEmail.value = SgViewState(Unit)
                }, {
                    liveCredentialResetEmail.value = SgViewState(it as SgError)
                })
    }

    fun refreshLastUserCredentials() {

        (liveLastCredentials as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.provideLastUserCredentials()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveLastCredentials.value = SgViewState(it)
                }, {
                    liveLastCredentials.value = SgViewState(it as SgError)
                })
    }

    fun confirmTfaSecretChange(tfaCode: CharSequence) {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.confirmTfaSecretChange(tfaCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }
}