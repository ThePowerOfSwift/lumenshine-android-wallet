package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.domain.data.RegistrationStatus
import com.soneso.lumenshine.domain.data.SgError
import com.soneso.lumenshine.domain.data.TfaSecret
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
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

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it ?: return@Observer)
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

    private fun renderTfaSecret(viewState: SgViewState<TfaSecret>) {

        when (viewState.state) {
            State.ERROR -> {
                handleError(viewState.error)
            }
            State.LOADING -> {
            }
            State.READY -> {
                setupToken(viewState.data!!)
            }
        }
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {
        when (viewState.state) {
            State.ERROR -> {
                hideProgressDialog()
                handleError(viewState.error)
            }
            State.LOADING -> {
                context?.showProgressDialog()
            }
            State.READY -> {
                hideProgressDialog()
            }
        }
    }

    private fun showLoadingButton(loading: Boolean) {
        if (loading) {
            change_tfa_progress.visibility = View.VISIBLE
            send_button.visibility = View.INVISIBLE
        } else {
            change_tfa_progress.visibility = View.GONE
            send_button.visibility = View.VISIBLE
        }
    }

    /**
     * handling response errors
     */
    private fun handleError(e: SgError?) {
        val error = e ?: return

        when (error.errorCode) {
            ErrorCodes.LOGIN_INVALID_2FA -> {
                tfa_code_view.error = if (error.errorResId == 0) error.message!! else getString(error.errorResId)
            }
            else -> {
                showErrorSnackbar(error)
            }
        }
    }

    private fun setupToken(tfaSecret: TfaSecret) {

//        qr_code_view.post {
//            val params = qr_code_view.layoutParams
//            params.height = qr_code_view.width
//            qr_code_view.requestLayout()
//
//            qr_code_view.setImageBitmap(BitmapFactory.decodeByteArray(tfaSecret.imageData, 0, tfaSecret.imageData.size))
//        }
        token_view.keyListener = null
        token_view.text = tfaSecret.secretCode
        copy_button.setOnClickListener {

            val clipboard = context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", tfaSecret.secretCode)
            clipboard.primaryClip = clip
            showSnackbar(getString(R.string.secret_copied))
        }
    }

    companion object {

        const val TAG = "TfaConfirmationFragment"

        fun newInstance() = TfaConfirmationFragment()
    }
}
