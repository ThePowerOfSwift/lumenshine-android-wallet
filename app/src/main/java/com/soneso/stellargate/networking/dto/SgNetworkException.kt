package com.soneso.stellargate.networking.dto

class SgNetworkException(val validationErrors: List<ValidationError>?, throwable: Throwable?) : Exception(throwable) {

    constructor(throwable: Throwable?) : this(null, throwable)

    constructor(validationErrors: List<ValidationError>?) : this(validationErrors, null)
}