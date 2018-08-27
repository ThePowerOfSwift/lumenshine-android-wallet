package com.soneso.lumenshine.networking.dto

import com.soneso.lumenshine.networking.dto.exceptions.ValidationError

class SgNetworkException(val validationErrors: List<ValidationError>?, throwable: Throwable?) : Exception(throwable) {

    constructor(throwable: Throwable?) : this(null, throwable)

    constructor(validationErrors: List<ValidationError>?) : this(validationErrors, null)
}