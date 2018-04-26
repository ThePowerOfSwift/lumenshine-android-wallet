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

    fun createAccount(email: CharSequence, password: CharSequence) = authUseCases.generateAccount(email, password)

    fun confirmTfaRegistration(tfaCode: String) = authUseCases.confirmTfaRegistration(tfaCode)

    fun refreshSalutations() {
        authUseCases.provideSalutations()
                .subscribe({
                    (liveSalutations as MutableLiveData).value = it
                }, {
                    setError(it as SgError)
                })
    }
}