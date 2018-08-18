package com.soneso.lumenshine.presentation.settings

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isNotEmpty
import com.soneso.lumenshine.R
import com.soneso.lumenshine.presentation.MainActivity
import com.soneso.lumenshine.presentation.general.SgActivity
import com.soneso.lumenshine.presentation.general.SgViewState
import kotlinx.android.synthetic.main.view_change_password.*
import com.soneso.lumenshine.presentation.general.State
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showInfoDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.view_change_password_success.*

class ChangePasswordActivity : SgActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]
        subscribeForLiveData()
        setupListeners()
    }

    private fun setupListeners() {

        change_pass_button.setOnClickListener { attemptPassChange() }
        password_info_button.setOnClickListener {
            showInfoDialog(R.string.password_requirements, R.layout.info_password)
        }
        change_pass_home_button.setOnClickListener {
            finishAffinity()
            MainActivity.startInstance(this)
        }
        change_pass_settings_button.setOnClickListener {
            finish()

        }
    }

    private fun subscribeForLiveData() {

        viewModel.livePasswordChange.observe(this, Observer {
            renderPassChange(it ?: return@Observer)
        })
    }

    private fun attemptPassChange() {

        if (isValidForm()) {
            viewModel.changePassword(current_pass.trimmedText, new_pass.trimmedText)
        }
    }

    private fun isValidForm() =
            current_pass.text.isNotEmpty()
                    && new_pass.isValidPassword()
                    && isPasswordMatch()


    private fun isPasswordMatch(): Boolean {
        val match = new_pass.trimmedText == pass_confirmation.trimmedText
        if (!match) {
            pass_confirmation.error = getString(R.string.password_not_match)
        }
        return match
    }

    private fun renderPassChange(viewState: SgViewState<Unit>) {

        when (viewState.state) {

            State.READY -> {
                hideProgressDialog()
                change_password_view.visibility = View.GONE
                change_password_success_view.visibility = View.VISIBLE

            }
            State.LOADING -> {
                showProgressDialog()
            }
            State.ERROR -> {
                hideProgressDialog()
                if (viewState.error!!.errorCode != 0)
                    current_pass.error = getString(R.string.error_invalid_current_password)
                else {
                    showErrorSnackbar(viewState.error)
                }
            }
        }
    }
}
