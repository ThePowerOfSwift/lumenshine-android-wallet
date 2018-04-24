package com.soneso.stellargate.model.dto

import com.soneso.stellargate.model.dto.auth.ValidationError
import java.util.*

class SgNetworkError(localType: LocalErrorType = LocalErrorType.NONE) : LinkedList<ValidationError>() {

    private val localErrorType = localType

    fun isNoInternetError() = localErrorType == LocalErrorType.NO_INTERNET

    fun isUnknownError() = localErrorType == LocalErrorType.UNKNOWN
}

enum class LocalErrorType {
    NONE, UNKNOWN, NO_INTERNET
}