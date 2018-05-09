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
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class AuthViewModel(private val authUseCases: AuthUseCases) : ViewModel() {

    val liveSalutations: LiveData<SgViewState<List<String>>> = MutableLiveData()

    val liveTfaSecret: LiveData<SgViewState<String>> = MutableLiveData()

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

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.registerAccount(email, password, country)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun confirmTfaRegistration(tfaCode: String) {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.confirmTfaRegistration(tfaCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun refreshSalutations() {

        (liveSalutations as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideSalutations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveSalutations.value = SgViewState(it)
                }, {
                    liveSalutations.value = SgViewState(it as SgError)
                })
    }

    fun refreshCountries() {

        (liveCountries as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideCountries()
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

        authUseCases.login(email, password, tfa)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun fetchMnemonic() {

        (liveMnemonic as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideMnemonicForCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveMnemonic.value = SgViewState(it)
                }, {
                    liveMnemonic.value = SgViewState(SgError(R.string.unknown_error))
                })
    }

    fun confirmMnemonic() {

        (liveMnemonicConfirmation as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.confirmMnemonic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveMnemonicConfirmation.value = SgViewState(Unit)
                }, {
                    liveMnemonicConfirmation.value = SgViewState(it as SgError)
                })
    }

    fun resendConfirmationMail() {

        (liveConfirmationMail as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.resendConfirmationMail()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveConfirmationMail.value = SgViewState(Unit)
                }, {
                    liveConfirmationMail.value = SgViewState(it as SgError)
                })
    }

    fun refreshRegistrationStatus() {

        (liveRegistrationStatus as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideRegistrationStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveRegistrationStatus.value = SgViewState(it)
                }, {
                    liveRegistrationStatus.value = SgViewState(it as SgError)
                })
    }

    fun fetchTfaSecret() {

        (liveTfaSecret as MutableLiveData).value = SgViewState(State.LOADING)

        authUseCases.provideTfaSecret()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveTfaSecret.value = SgViewState(it)
                }, {
                    liveTfaSecret.value = SgViewState(it as SgError)
                })
    }
}