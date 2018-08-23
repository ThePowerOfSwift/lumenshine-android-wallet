package com.soneso.lumenshine.util

import com.soneso.lumenshine.networking.ApiResource
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import io.reactivex.Flowable
import io.reactivex.Single

fun <SuccessType, NewSuccessType, NewFailureType> Flowable<ApiResource<SuccessType>>.mapResource(
        successMapper: ((SuccessType) -> NewSuccessType),
        failureMapper: ((ServerException) -> NewFailureType)
): Flowable<Resource<NewSuccessType, NewFailureType>> {
    return map {
        it.map(successMapper, failureMapper)
    }
}

fun <SuccessType, FailureType, NewSuccessType, NewFailureType> Single<Resource<SuccessType, FailureType>>.mapResource(
        successMapper: ((SuccessType) -> NewSuccessType),
        failureMapper: ((FailureType) -> NewFailureType)
): Single<Resource<NewSuccessType, NewFailureType>> {
    return map {
        it.map(successMapper, failureMapper)
    }
}