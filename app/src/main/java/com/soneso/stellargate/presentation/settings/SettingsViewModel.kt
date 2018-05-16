package com.soneso.stellargate.presentation.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.domain.data.SgError
import com.soneso.stellargate.domain.usecases.UserUseCases
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
import io.reactivex.android.schedulers.AndroidSchedulers

class SettingsViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val livePasswordChange: LiveData<SgViewState<Unit>> = MutableLiveData()

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
}