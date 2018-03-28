package com.soneso.stellargate.model.dto

import android.arch.lifecycle.MutableLiveData

class DataProvider<DataType> {

    var errorMessage = ""
    var data: DataType? = null
    var liveStatus: MutableLiveData<DataStatus> = MutableLiveData()
}

enum class DataStatus {
    LOADING, SUCCESS, ERROR
}