package com.soneso.lumenshine.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.soneso.lumenshine.R
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.MainActivity
import com.soneso.lumenshine.presentation.general.LsActivity
import com.soneso.lumenshine.presentation.util.showInfoDialog
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.view_change_password.*
import kotlinx.android.synthetic.main.view_change_password_success.*

class ChangePasswordActivity : LsActivity() {

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

    private fun renderPassChange(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {

            Resource.SUCCESS -> {
                change_password_view.visibility = View.GONE
                change_password_success_view.visibility = View.VISIBLE

            }
            Resource.LOADING -> {
            }
            Resource.FAILURE -> {
                showErrorSnackbar(resource.failure())
            }
        }
    }
}
