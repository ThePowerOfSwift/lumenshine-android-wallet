package com.soneso.lumenshine.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soneso.lumenshine.domain.usecases.UserUseCases
import com.soneso.lumenshine.presentation.util.putValue
import com.soneso.lumenshine.util.LsException
import com.soneso.lumenshine.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LostCredentialViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val liveCredentialResetEmail: LiveData<Resource<Boolean, LsException>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

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

}