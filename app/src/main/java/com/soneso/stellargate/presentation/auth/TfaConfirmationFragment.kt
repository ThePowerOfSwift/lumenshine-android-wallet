package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.authenticator.OtpProvider
import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
import com.soneso.stellargate.presentation.util.displayQrCode
import kotlinx.android.synthetic.main.fragment_tfa_registration.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TfaConfirmationFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_tfa_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.fetchTfaSecret()
        subscribeForLiveData()
        setupListeners()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveTfaSecret.observe(this, Observer {
            renderTfaSecret(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        send_button.setOnClickListener {

            when (authActivity.useCase) {
                AuthActivity.UseCase.AUTH -> {
                    authViewModel.confirmTfaRegistration(tfa_code_view.text.toString())
                }
                AuthActivity.UseCase.CONFIRM_TFA_SECRET_CHANGE -> {
                    authViewModel.confirmTfaSecretChange(tfa_code_view.text.toString())
                }
            }
        }
    }

    private fun renderTfaSecret(viewState: SgViewState<String>) {

        when (viewState.state) {
            State.ERROR -> {

                showErrorSnackbar(viewState.error)
            }
            State.LOADING -> {
            }
            State.READY -> {

                setupToken(viewState.data!!)
            }
        }
    }

    private fun setupToken(token: String) {

        qr_code_view.post {
            val params = qr_code_view.layoutParams
            params.height = qr_code_view.width
            qr_code_view.requestLayout()
            qr_code_view.displayQrCode(token)
        }
        token_view.keyListener = null
        token_view.setText(token)
        copy_button.setOnClickListener {

            val clipboard = context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", OtpProvider.currentTotpCode(token))
            clipboard.primaryClip = clip
            showSnackbar("Token copied in clipboard!")
        }
    }

    companion object {

        const val TAG = "TfaConfirmationFragment"

        fun newInstance() = TfaConfirmationFragment()
    }
}
