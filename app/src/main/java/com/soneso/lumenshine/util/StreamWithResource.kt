package com.soneso.lumenshine.util

import io.reactivex.Flowable
import io.reactivex.Single

fun <SuccessType, FailureType, NewSuccessType, NewFailureType> Flowable<Resource<SuccessType, FailureType>>.mapResource(
        successMapper: ((SuccessType) -> NewSuccessType),
        failureMapper: ((FailureType) -> NewFailureType)
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