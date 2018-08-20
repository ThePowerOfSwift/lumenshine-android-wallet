package com.soneso.lumenshine.networking.dto.exceptions

import okhttp3.ResponseBody

class ServerException(errorBody: ResponseBody?, throwable: Throwable? = null) {

    constructor(throwable: Throwable?) : this(null, throwable)
}