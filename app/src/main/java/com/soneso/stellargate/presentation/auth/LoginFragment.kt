package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.authenticator.OtpProvider
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.DashboardStatus
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : AuthFragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(authActivity, viewModelFactory)[AuthViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tfaSecret = SgPrefs.tfaSecret
        if (tfaSecret.isNotEmpty()) {
            val otpProvider = OtpProvider(context!!)
            Log.d(TAG, otpProvider.getCurrentTotpCode(tfaSecret, null))
        }

        subscribeForLiveData()
        setupListeners()
    }

    private fun subscribeForLiveData() {
        authViewModel.liveDashboardStatus.observe(this, Observer {
            renderDashboardStatus(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.setOnClickListener { attemptLogin() }
        create_account_button.setOnClickListener {
            replaceFragment(RegistrationFragment.newInstance(), RegistrationFragment.TAG)
        }
    }

    private fun attemptLogin() {

        if (!isValidForm()) {
            return
        }

        authViewModel.loginWithTfa(email.trimmedText, password.trimmedText, two_factor_code.trimmedText)
    }

    private fun showLoadingButton(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
            email_sign_in_button.visibility = View.INVISIBLE
        } else {
            progress_bar.visibility = View.GONE
            email_sign_in_button.visibility = View.VISIBLE
        }
    }

    private fun renderDashboardStatus(viewState: SgViewState<DashboardStatus>) {

        when (viewState.state) {
            State.LOADING -> {

                showLoadingButton(true)
            }
            State.READY -> {

                showLoadingButton(false)
                // TODO: cristi.paval, 4/27/18 - handle here if the user has to confirm the email or has to confirm the mnemonic
            }
            State.ERROR -> {

                showLoadingButton(false)
                showErrorSnackbar(viewState.error)
            }
        }
    }

    private fun isValidForm() =
            email.hasValidInput()
                    && password.hasValidInput()

    companion object {

        const val TAG = "LoginFragment"

        fun newInstance() = LoginFragment()
    }
}
