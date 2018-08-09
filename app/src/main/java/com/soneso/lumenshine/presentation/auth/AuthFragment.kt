package com.soneso.lumenshine.presentation.auth

import com.soneso.lumenshine.presentation.general.SgFragment

/**
 * Fragment.
 * Created by cristi.paval on 3/21/18.
 */
open class AuthFragment : SgFragment() {

    protected val authViewModel: AuthViewModel
        get() = authActivity.authViewModel

    protected val authActivity: AuthActivity
        get() = activity as AuthActivity

    fun replaceFragment(fragment: AuthFragment, tag: String) {

        authActivity.replaceFragment(fragment, tag)
    }
}