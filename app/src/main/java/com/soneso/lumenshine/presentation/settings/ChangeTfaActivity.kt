package com.soneso.lumenshine.presentation.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isNotEmpty
import com.google.authenticator.OtpProvider
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.domain.data.RegistrationStatus
import com.soneso.lumenshine.domain.data.SgError
import com.soneso.lumenshine.domain.data.TfaSecret
import com.soneso.lumenshine.presentation.MainActivity
import com.soneso.lumenshine.presentation.general.SgActivity
import com.soneso.lumenshine.presentation.general.SgViewState
import kotlinx.android.synthetic.main.view_change_tfa_new_secret.*
import kotlinx.android.synthetic.main.view_change_tfa_password_confirm.*
import com.soneso.lumenshine.presentation.general.State
import kotlinx.android.synthetic.main.activity_change_tfa.*
import kotlinx.android.synthetic.main.view_change_tfa_success.*

class ChangeTfaActivity : SgActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_tfa)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]
        subscribeForLiveData()
        setupListeners()
    }

    private fun setupListeners() {

        change_tfa_password_confirm.setOnClickListener {
            attemptTfaChange()
        }

        change_tfa_next_button.setOnClickListener {
            attemptTfaChangeConfirm()
        }

        change_tfa_cancel_button.setOnClickListener {
            finish()
        }

        change_tfa_home_button.setOnClickListener {
            finishAffinity()
            MainActivity.startInstance(this)
        }

        change_tfa_settings_button.setOnClickListener {
            finish()
        }

    }

    private fun subscribeForLiveData() {

        viewModel.liveTfaSecret.observe(this, Observer {
            renderTfaChange(it ?: return@Observer)
        })

        viewModel.liveRegistrationStatus.observe(this, Observer {
            renderTfaChangeConfirmation(it ?: return@Observer)
        })

    }

    private fun attemptTfaChange() {
        if (change_tfa_current_pass.hasValidInput()) {
            viewModel.changeTfaSecret(change_tfa_current_pass.trimmedText)
        }
    }

    private fun attemptTfaChangeConfirm() {
        if (tfa_code_view.text.isNotEmpty()) {
            viewModel.confirmTfaSecretChange(tfa_code_view.text.toString())
        } else {
            tfa_code_view.error = getString(R.string.error_field_required)
        }
    }


    private fun renderTfaChange(viewState: SgViewState<TfaSecret>) {

        when (viewState.state) {

            State.READY -> {
                change_tfa_password_confirm.visibility = View.VISIBLE
                change_tfa_pass_confirm_progress.visibility = View.GONE
                change_tfa_password_confirm_view.visibility = View.GONE
                change_tfa_new_secret_view.visibility = View.VISIBLE
                setupToken(viewState.data!!)
            }
            State.LOADING -> {

                change_tfa_password_confirm.visibility = View.INVISIBLE
                change_tfa_pass_confirm_progress.visibility = View.VISIBLE
            }
            State.ERROR -> {

                change_tfa_password_confirm.visibility = View.VISIBLE
                change_tfa_pass_confirm_progress.visibility = View.GONE

                handleError(viewState.error)
            }
        }
    }

    private fun renderTfaChangeConfirmation(viewState: SgViewState<RegistrationStatus>) {
        when (viewState.state) {
            State.READY -> {
                change_tfa_next_button.visibility = View.VISIBLE
                change_tfa_progress.visibility = View.GONE

                if (viewState.data!!.tfaConfirmed) {

                    change_tfa_new_secret_view.visibility = View.GONE
                    change_tfa_success_view.visibility = View.VISIBLE

                } else {
                    showSnackbar(getString(R.string.failed_to_change_tfa))
                }

            }

            State.LOADING -> {
                change_tfa_next_button.visibility = View.INVISIBLE
                change_tfa_progress.visibility = View.VISIBLE
            }

            State.ERROR -> {
                change_tfa_next_button.visibility = View.VISIBLE
                change_tfa_progress.visibility = View.GONE
                handleError(viewState.error)
            }
        }
    }

    /**
     * handling response errors
     */
    private fun handleError(e: SgError?) {
        val error = e ?: return

        when (error.errorCode) {
            ErrorCodes.LOGIN_WRONG_PASSWORD -> {
                change_tfa_current_pass.error = if (error.errorResId == 0) error.message!! else getString(error.errorResId)
            }
            ErrorCodes.LOGIN_INVALID_2FA ->{
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

            val clipboard = getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", tfaSecret.secretCode)
            clipboard.primaryClip = clip
            showSnackbar(getString(R.string.secret_copied))
        }
    }
}
