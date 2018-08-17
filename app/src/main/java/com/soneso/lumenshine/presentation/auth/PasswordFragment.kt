package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.authenticator.OtpProvider
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.domain.data.RegistrationStatus
import com.soneso.lumenshine.domain.data.SgError
import com.soneso.lumenshine.persistence.SgPrefs
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import com.soneso.lumenshine.presentation.util.decodeBase32
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
import kotlinx.android.synthetic.main.fragment_password.*


class PasswordFragment : AuthFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        subscribeForLiveData()
    }

    private fun setupListeners() {

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        submit_button.setOnClickListener { attemptLogin() }
        lost_password_button.setOnClickListener {
            SgPrefs.removeUserCrendentials()
            authViewModel.refreshLastUserCredentials()
            replaceFragment(LostCredentialFragment.newInstance(LostCredentialFragment.Credential.PASSWORD), LostCredentialFragment.TAG)
        }
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it ?: return@Observer)
        })
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {

        when (viewState.state) {
            State.LOADING -> {

                context?.showProgressDialog()
            }
            State.ERROR -> {

                hideProgressDialog()
                handleError(viewState.error)
            }
            else -> {

                hideProgressDialog()
            }
        }
    }

    /**
     * handling login response errors
     */
    private fun handleError(e: SgError?) {
        val error = e ?: return

        when (error.errorCode) {
            ErrorCodes.LOGIN_EMAIL_NOT_EXIST -> {
                showErrorSnackbar(error)
            }
            ErrorCodes.LOGIN_INVALID_2FA -> {
                showErrorSnackbar(error)
            }
            ErrorCodes.LOGIN_WRONG_PASSWORD -> {
                password.error = if (error.errorResId == 0) error.message!! else getString(error.errorResId)
            }
            else -> {
                showErrorSnackbar(error)
            }
        }
    }

    private fun attemptLogin() {

        val credentials = authViewModel.liveLastCredentials.value?.data ?: return
        val tfaCode = OtpProvider.currentTotpCode(credentials.tfaSecret.decodeBase32()) ?: return

        authViewModel.login(credentials.username, password.trimmedText, tfaCode)
    }

    companion object {
        const val TAG = "PasswordFragment"

        fun newInstance() = PasswordFragment()
    }


}
