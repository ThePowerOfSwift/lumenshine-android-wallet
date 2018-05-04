package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.RegistrationStatus
import com.soneso.stellargate.presentation.general.SgActivity
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State

/**
 * A login screen that offers login via email/password.
 */
class AuthActivity : SgActivity() {

    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        title = getString(R.string.action_sign_in)

        subscribeForLiveData()

        replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it ?: return@Observer)
        })
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {

        when (viewState.state) {
            State.READY -> {

                handleRegistrationStatus(viewState.data!!)
            }
            else -> {
                // cristi.paval, 5/3/18 - this should be rendered in subclasses
            }
        }
    }

    private fun handleRegistrationStatus(status: RegistrationStatus) {

        when {
            !status.tfaConfirmed -> {

                replaceFragment(TfaRegistrationFragment.newInstance(), TfaRegistrationFragment.TAG)
            }
            !status.emailConfirmed -> {

                replaceFragment(MailConfirmationFragment.newInstance(), MailConfirmationFragment.TAG)
            }
            !status.mnemonicConfirmed -> {

                replaceFragment(MnemonicFragment.newInstance(), MnemonicFragment.TAG)
            }
        }
    }


    fun replaceFragment(fragment: AuthFragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
    }
}
