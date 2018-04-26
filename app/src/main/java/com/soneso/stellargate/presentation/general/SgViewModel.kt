package com.soneso.stellargate.presentation.general

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.stellargate.domain.data.SgError

open class SgViewModel : ViewModel() {

    val liveError: LiveData<SgError> = MutableLiveData()

    protected fun setError(error: SgError) {
        (liveError as MutableLiveData).value = error
    }
}