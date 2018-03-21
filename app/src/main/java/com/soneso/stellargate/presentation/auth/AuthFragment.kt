package com.soneso.stellargate.presentation.auth

import com.soneso.stellargate.presentation.SgFragment

/**
 * Fragment.
 * Created by cristi.paval on 3/21/18.
 */
open class AuthFragment : SgFragment() {

    val authActivity: AuthActivity
        get() = activity as AuthActivity


    fun replaceFragment(fragment: AuthFragment, tag: String) {
        authActivity.replaceFragment(fragment, tag)
    }
}