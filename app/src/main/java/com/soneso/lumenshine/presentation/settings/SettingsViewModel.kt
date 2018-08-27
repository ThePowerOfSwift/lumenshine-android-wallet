package com.soneso.lumenshine.presentation.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.lumenshine.domain.data.SgError
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import com.soneso.lumenshine.presentation.util.putValue
import com.soneso.lumenshine.util.LsException
import com.soneso.lumenshine.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SettingsViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val livePasswordChange: LiveData<SgViewState<Unit>> = MutableLiveData()

    val liveTfaSecret: LiveData<Resource<String, LsException>> = MutableLiveData()

    val liveTfaChangeConfirmation: LiveData<Resource<Boolean, LsException>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

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

        val d = userUseCases.changeTfaPassword(pass)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveTfaSecret.putValue(it)
                }
        compositeDisposable.add(d)
    }

    fun confirmTfaSecretChange(tfaCode: CharSequence) {

        val d = userUseCases.confirmTfaSecretChange(tfaCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    liveTfaChangeConfirmation.putValue(it)
                }
        compositeDisposable.add(d)
    }

    override fun onCleared() {

        compositeDisposable.dispose()
        super.onCleared()
    }
}