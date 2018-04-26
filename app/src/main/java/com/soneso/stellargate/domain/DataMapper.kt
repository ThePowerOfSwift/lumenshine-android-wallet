package com.soneso.stellargate.domain

import com.soneso.stellargate.model.dto.Parse
import com.soneso.stellargate.model.dto.SgNetworkException
import com.soneso.stellargate.model.dto.auth.ValidationError
import io.reactivex.functions.Function
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.Result

class DataMapper<T> : Function<Result<T>, T> {

    override fun apply(result: Result<T>): T {

        if (result.isError || result.response() == null) {
            throw SgNetworkException(result.error())

        } else {
            val response = result.response()!!
            if (response.isSuccessful) {
                return response.body()!!

            } else {
                throw handleError(response.errorBody())
            }
        }
    }

    private fun handleError(errorBody: ResponseBody?): Throwable {
        if (errorBody == null) {
            return SgNetworkException(NullPointerException())
        }
        val javaType = OBJECT_MAPPER.typeFactory.constructCollectionType(List::class.java, ValidationError::class.java)
        return SgNetworkException(OBJECT_MAPPER.readValue<List<ValidationError>>(errorBody.string(), javaType))
    }

    companion object {
        const val TAG = "DataMapper"
        val OBJECT_MAPPER = Parse.createMapper()
    }
}