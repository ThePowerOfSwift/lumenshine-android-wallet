package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.authenticator.OtpProvider

import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.RegistrationStatus
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
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

    private fun showLoadingButton(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
            submit_button.visibility = View.INVISIBLE
        } else {
            progress_bar.visibility = View.GONE
            submit_button.visibility = View.VISIBLE
        }
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {

        when (viewState.state) {
            State.LOADING -> {

                showLoadingButton(true)
            }
            State.ERROR -> {

                showLoadingButton(false)
                showErrorSnackbar(viewState.error)
            }
            else -> {

                showLoadingButton(false)
            }
        }
    }

    private fun attemptLogin() {

        if (password.isValidPassword().not()) {
            return
        }


        val credentials = authViewModel.liveLastCredentials.value?.data ?: return
        val tfaCode = OtpProvider.currentTotpCode(credentials.tfaSecret) ?: return

        authViewModel.login(credentials.username, password.trimmedText, tfaCode)
    }

    companion object {
        const val TAG = "PasswordFragment"

        fun newInstance() = PasswordFragment()
    }


}
