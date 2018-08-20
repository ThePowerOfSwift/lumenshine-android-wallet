package com.soneso.lumenshine.util

import android.support.annotation.IntDef

open class Resource<SuccessType, FailureType>(
        @State val state: Int,
        private val data: SuccessType? = null,
        private val failure: FailureType? = null
) {

    fun failure() = failure!!

    fun success() = data!!

    fun <NewSuccessType, NewFailureType> map(
            successMapper: ((SuccessType) -> NewSuccessType),
            failureMapper: ((FailureType) -> NewFailureType)
    ): Resource<NewSuccessType, NewFailureType> {
        return when (state) {

            SUCCESS -> Success(successMapper.invoke(success()))
            FAILURE -> Failure(failureMapper.invoke(failure()))
            else -> Resource(LOADING)
        }
    }

    companion object {

        const val LOADING = 0
        const val SUCCESS = 1
        const val FAILURE = 2
    }
}

class Success<SuccessType, FailureType>(data: SuccessType) : Resource<SuccessType, FailureType>(SUCCESS, data, null)

class Failure<SuccessType, FailureType>(failure: FailureType) : Resource<SuccessType, FailureType>(FAILURE, null, failure)

@IntDef(Resource.LOADING, Resource.SUCCESS, Resource.FAILURE)
@Retention(AnnotationRetention.SOURCE)
annotation class State