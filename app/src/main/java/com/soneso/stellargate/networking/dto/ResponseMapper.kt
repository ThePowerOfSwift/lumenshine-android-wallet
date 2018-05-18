package com.soneso.stellargate.networking.dto

import okhttp3.ResponseBody
import retrofit2.Response

class ResponseMapper<T> : ResultMapper<T>() {

    override fun handleSuccess(response: Response<T>): T = response.body()!!

    override fun handleError(errorBody: ResponseBody?): Throwable {

        if (errorBody == null) {
            return SgNetworkException(Exception())
        }

        return try {
            val javaType = OBJECT_MAPPER.typeFactory.constructCollectionType(List::class.java, ValidationError::class.java)
            SgNetworkException(OBJECT_MAPPER.readValue<List<ValidationError>>(errorBody.string(), javaType))

        } catch (e1: Exception) {

            try {
                SgNetworkException(listOf(OBJECT_MAPPER.readValue(errorBody.string(), ValidationError::class.java)))

            } catch (e2: Exception) {
                SgNetworkException(e2)
            }
        }
    }

    companion object {
        const val TAG = "ResponseMapper"
        val OBJECT_MAPPER = Parse.createMapper()
    }
}