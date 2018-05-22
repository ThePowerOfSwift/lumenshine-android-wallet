package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.RegistrationStatus
import com.soneso.stellargate.presentation.MainActivity
import com.soneso.stellargate.presentation.general.SgActivity
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State

/**
 * A login screen that offers login via email/password.
 */
class AuthActivity : SgActivity() {

    lateinit var authViewModel: AuthViewModel
        private set
    lateinit var useCase: UseCase
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        title = getString(R.string.action_sign_in)

        subscribeForLiveData()
        useCase = intent?.getSerializableExtra(EXTRA_USE_CASE) as? UseCase ?: UseCase.AUTH

        startPage()
    }

    private fun startPage() {

        when (useCase) {
            UseCase.AUTH -> {
                replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
            }
            UseCase.CONFIRM_TFA_SECRET_CHANGE -> {
                replaceFragment(TfaConfirmationFragment.newInstance(), TfaConfirmationFragment.TAG)
            }
        }
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

                replaceFragment(TfaConfirmationFragment.newInstance(), TfaConfirmationFragment.TAG)
            }
            !status.emailConfirmed -> {

                replaceFragment(MailConfirmationFragment.newInstance(), MailConfirmationFragment.TAG)
            }
            !status.mnemonicConfirmed -> {

                replaceFragment(MnemonicFragment.newInstance(), MnemonicFragment.TAG)
            }
            else -> {

                handleRegistrationCompleted()
            }
        }
    }

    private fun handleRegistrationCompleted() {

        when (useCase) {
            UseCase.AUTH -> {
                finishAffinity()
                MainActivity.startInstance(this)
            }
            UseCase.CONFIRM_TFA_SECRET_CHANGE -> {
                finish()
            }
        }
    }


    fun replaceFragment(fragment: AuthFragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
    }

    companion object {

        const val TAG = "AuthActivity"
        private const val EXTRA_USE_CASE = "$TAG.EXTRA_USE_CASE"

        fun startInstance(context: Context, page: UseCase) {
            val intent = Intent(context, AuthActivity::class.java)
            intent.putExtra(EXTRA_USE_CASE, page)
            context.startActivity(intent)
        }
    }

    enum class UseCase {
        AUTH, CONFIRM_TFA_SECRET_CHANGE
    }
}
