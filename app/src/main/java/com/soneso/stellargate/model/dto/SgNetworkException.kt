package com.soneso.stellargate.model.dto

import com.soneso.stellargate.model.dto.auth.ValidationError

class SgNetworkException(val validationErrors: List<ValidationError>?, throwable: Throwable?) : Exception(throwable) {

    constructor(throwable: Throwable?) : this(null, throwable)

    constructor(validationErrors: List<ValidationError>?) : this(validationErrors, null)
}