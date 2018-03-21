package com.soneso.stellargate.presentation.auth

import android.os.Bundle
import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.SgActivity

/**
 * A login screen that offers login via email/password.
 */
class AuthActivity : SgActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = getString(R.string.action_sign_in)

        replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
    }


    fun replaceFragment(fragment: AuthFragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
    }
}
