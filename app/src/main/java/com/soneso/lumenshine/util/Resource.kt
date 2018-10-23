package com.soneso.lumenshine.util

import androidx.annotation.IntDef

open class Resource<SuccessType, FailureType>(
        @State val state: Int,
        private val data: SuccessType? = null,
        private val failure: FailureType? = null
) {

    fun failure() = failure!!

    fun success() = data!!

    val isSuccessful: Boolean
        get() = state == SUCCESS

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

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [Resource.LOADING, Resource.SUCCESS, Resource.FAILURE])
annotation class State
