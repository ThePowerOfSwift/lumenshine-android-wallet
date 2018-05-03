package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.Country
import com.soneso.stellargate.domain.data.RegistrationStatus
import com.soneso.stellargate.domain.data.SgError
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class AuthViewModel(private val authUseCases: AuthUseCases) : ViewModel() {

    val liveSalutations: LiveData<SgViewState<List<String>>> = MutableLiveData()

    val liveTfaSecret: LiveData<SgViewState<String>> = MutableLiveData()

    val liveConfirmation: LiveData<SgViewState<Unit>> = MutableLiveData()

    val liveCountries: LiveData<SgViewState<List<Country>>> = MutableLiveData()

    val liveRegistrationStatus: LiveData<SgViewState<RegistrationStatus>> = MutableLiveData()

    val liveMnemonic: LiveData<SgViewState<String>> = MutableLiveData()

    val liveMnemonicConfirmation: LiveData<SgViewState<Unit>> = MutableLiveData()

    val liveConfirmationMail: LiveData<SgViewState<Unit>> = MutableLiveData()

    fun createAccount(email: CharSequence, password: CharSequence, countryPosition: Int) {

        val country = try {
            liveCountries.value?.data?.get(countryPosition)
        } catch (ignored: Exception) {
            null
        }

        (liveTfaSecret as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.generateAccount(email, password, country)
                .subscribe({
                    liveTfaSecret.value = SgViewState(it)
                }, {
                    liveTfaSecret.value = SgViewState(it as SgError)
                })
    }

    fun confirmTfaRegistration(tfaCode: String) {

        (liveConfirmation as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.confirmTfaRegistration(tfaCode)
                .subscribe({
                    liveConfirmation.value = SgViewState(it)
                }, {
                    liveConfirmation.value = SgViewState(it as SgError)
                })
    }

    fun refreshSalutations() {

        (liveSalutations as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideSalutations()
                .subscribe({
                    liveSalutations.value = SgViewState(it)
                }, {
                    liveSalutations.value = SgViewState(it as SgError)
                })
    }

    fun refreshCountries() {

        (liveCountries as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideCountries()
                .subscribe({
                    liveCountries.value = SgViewState(it)
                }, {
                    liveCountries.value = SgViewState(it as SgError)
                })
    }

    fun loginWithTfa(email: CharSequence, password: CharSequence, tfaCode: CharSequence) {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.login(email, password, tfaCode)
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun fetchMnemonic(password: CharSequence) {

        (liveMnemonic as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideMnemonicForCurrentUser(password)
                .subscribe({
                    liveMnemonic.value = SgViewState(it)
                }, {
                    liveMnemonic.value = SgViewState(SgError(R.string.unknown_error))
                })
    }

    fun confirmMnemonic() {

        (liveMnemonicConfirmation as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.confirmMnemonic()
                .subscribe({
                    liveMnemonicConfirmation.value = SgViewState(Unit)
                }, {
                    liveMnemonicConfirmation.value = SgViewState(it as SgError)
                })
    }

    fun resendConfirmationMail() {

        (liveConfirmationMail as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.resendConfirmationMail()
                .subscribe({
                    liveConfirmationMail.value = SgViewState(Unit)
                }, {
                    liveConfirmationMail.value = SgViewState(it as SgError)
                })
    }

    fun refreshRegistrationStatus() {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideRegistrationStatus()
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }
}