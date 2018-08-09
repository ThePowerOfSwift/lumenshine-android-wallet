package com.soneso.lumenshine.presentation.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.lumenshine.domain.data.SgError
import com.soneso.lumenshine.domain.data.TfaSecret
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import io.reactivex.android.schedulers.AndroidSchedulers

class SettingsViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val livePasswordChange: LiveData<SgViewState<Unit>> = MutableLiveData()

    val liveTfaSecret: LiveData<SgViewState<TfaSecret>> = MutableLiveData()

    fun changePassword(oldPass: CharSequence, newPass: CharSequence) {

        (livePasswordChange as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.changeUserPassword(oldPass, newPass)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    livePasswordChange.value = SgViewState(Unit)
                }, {
                    livePasswordChange.value = SgViewState(it as SgError)
                })
    }

    fun changeTfaSecret(pass: CharSequence) {

        (liveTfaSecret as MutableLiveData).value = SgViewState(State.LOADING)

        userUseCases.changeTfaPassword(pass)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveTfaSecret.value = SgViewState(it)
                }, {
                    liveTfaSecret.value = SgViewState(it as SgError)
                })
    }
}