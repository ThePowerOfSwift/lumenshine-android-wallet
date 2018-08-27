package com.soneso.lumenshine.networking

import com.soneso.lumenshine.persistence.SgPrefs
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

/**
 * Object holding key data needed for getting the user data from server. Some data such as password are kept in RAM memory.
 */
object LsSessionProfile {

    const val TAG = "LsSessionProfile"

    var password: CharSequence = ""

    private val usernameSubject = PublishSubject.create<String>()

    var jwtToken: String = SgPrefs.jwtToken
        private set

    var tfaSecret: String = SgPrefs.tfaSecret
        private set

    const val langKey: String = "EN"

    init {
        SgPrefs.registerListener { key ->

            when (key) {
                SgPrefs.KEY_USERNAME -> {
                    usernameSubject.onNext(SgPrefs.username)
                }
                SgPrefs.KEY_JWT_TOKEN -> {
                    jwtToken = SgPrefs.jwtToken
                }
                SgPrefs.KEY_TFA_SECRET -> {
                    tfaSecret = SgPrefs.tfaSecret
                }
            }
        }
    }

    fun observeUsername(): Flowable<String> {

        return usernameSubject.toFlowable(BackpressureStrategy.LATEST)
                .startWith(SgPrefs.username)
                .distinctUntilChanged()
    }
}