package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import kotlinx.android.synthetic.main.fragment_finger_print.*


/**
 * A simple [Fragment] subclass.
 *
 */
class FingerPrintFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finger_print, container, false)
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
               handleError(viewState.error)
            }
            else -> {

                showLoadingButton(false)
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
        val tfaCode = OtpProvider.currentTotpCode(credentials.tfaSecret) ?: return

        authViewModel.login(credentials.username, password.trimmedText, tfaCode)

    }

    companion object {

        const val TAG = "FingerPrintFragment"
        private const val FRAGMENT_TYPE = "fragment_type"

        fun newInstance(fragmentType: FingerPrintFragment.FingerprintFragmentType): FingerPrintFragment {

            val instance = FingerPrintFragment()
            val args = Bundle()
            args.putSerializable(FRAGMENT_TYPE, fragmentType)
            instance.arguments = args
            return instance
        }
    }

    enum class FingerprintFragmentType {
        FINGERPRINT, FACE_ID
    }


}
