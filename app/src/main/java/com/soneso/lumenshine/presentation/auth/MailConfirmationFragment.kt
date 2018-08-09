package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.RegistrationStatus
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import kotlinx.android.synthetic.main.fragment_mail_confirmation.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MailConfirmationFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mail_confirmation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()
        setupListeners()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it ?: return@Observer)
        })
        authViewModel.liveConfirmationMail.observe(this, Observer {
            renderConfirmationMail(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        open_mail.setOnClickListener {
            openEmailApp()
        }
        resend_mail.setOnClickListener {
            authViewModel.resendConfirmationMail()
        }
        already_confirmed.setOnClickListener {
            authViewModel.refreshRegistrationStatus()
        }
    }

    private fun openEmailApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        if (intent.resolveActivity(context?.packageManager) != null) {
            startActivity(Intent.createChooser(intent, null))
        }
    }

    private fun renderConfirmationMail(viewState: SgViewState<Unit>) {
        when (viewState.state) {
            State.LOADING -> {
            }
            State.READY -> {

                showSnackbar("Mail sent!")
            }
            State.ERROR -> {

                showErrorSnackbar(viewState.error)
            }
        }
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {

        when (viewState.state) {
            State.LOADING -> {
                // cristi.paval, 5/3/18 - show here loading in ui
            }
            State.ERROR -> {

                showErrorSnackbar(viewState.error)
            }
            else -> {
                // cristi.paval, 5/3/18 - stop loading in ui
            }
        }
    }

    companion object {

        const val TAG = "MailConfirmationFragment"

        fun newInstance() = MailConfirmationFragment()
    }
}