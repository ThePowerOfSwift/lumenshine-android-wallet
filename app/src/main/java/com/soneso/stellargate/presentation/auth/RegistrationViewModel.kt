package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.soneso.stellargate.domain.data.SgError
import com.soneso.stellargate.domain.usecases.AuthUseCases
import com.soneso.stellargate.presentation.general.SgViewModel

/**
 * View model.
 * Created by cristi.paval on 3/22/18.
 */
class RegistrationViewModel(private val authUseCases: AuthUseCases) : SgViewModel() {

    val liveSalutations: LiveData<List<String>> = MutableLiveData()

    val liveTfaSecret: LiveData<String> = MutableLiveData()

    val liveConfirmation: LiveData<Boolean> = MutableLiveData()

    fun createAccount(email: CharSequence, password: CharSequence) {
        authUseCases.generateAccount(email, password)
                .subscribe({
                    (liveTfaSecret as MutableLiveData).value = it
                }, {
                    setError(it as SgError)
//                    setError(SgError("Mock Error"))
                })
    }

    fun confirmTfaRegistration(tfaCode: String) {
        authUseCases.confirmTfaRegistration(tfaCode)
                .subscribe({
                    (liveConfirmation as MutableLiveData).value = true
                }, {
                    setError(it as SgError)
                })
    }

    fun refreshSalutations() {
        authUseCases.provideSalutations()
                .subscribe({
                    (liveSalutations as MutableLiveData).value = it
                }, {
                    setError(it as SgError)
                })
    }
}