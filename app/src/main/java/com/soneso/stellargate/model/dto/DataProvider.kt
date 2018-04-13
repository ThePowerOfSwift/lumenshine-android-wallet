package com.soneso.stellargate.model.dto

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

class DataProvider<DataType> {

    var status: DataStatus?
        get() = liveStatus.value
        set(value) {
            (liveStatus as MutableLiveData).value = value
        }

    var errorMessage = ""
    var data: DataType? = null
    var liveStatus: LiveData<DataStatus> = MutableLiveData()
}

enum class DataStatus {
    LOADING, SUCCESS, ERROR
}