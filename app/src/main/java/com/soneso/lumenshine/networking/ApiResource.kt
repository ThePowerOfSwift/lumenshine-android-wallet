package com.soneso.lumenshine.networking

import com.soneso.lumenshine.util.Resource
import com.soneso.lumenshine.util.State
import com.soneso.lumenshine.networking.dto.exceptions.ServerException

open class ApiResource<T>(
        @State state: Int,
        data: T? = null,
        failure: ServerException? = null
) : Resource<T, ServerException>(state, data, failure)

class ApiSuccess<SuccessType>(data: SuccessType) : ApiResource<SuccessType>(SUCCESS, data, null)

class ApiFailure<SuccessType>(failure: ServerException) : ApiResource<SuccessType>(FAILURE, null, failure)