package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.util.Resource
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
        password.requestFocus()
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
            // TODO: cristi.paval, 8/25/18 - this anti pattern. Implement it accordingly.
//            SgPrefs.removeUserCrendentials()
//            authViewModel.refreshLastUserCredentials()
//            replaceFragment(LostCredentialFragment.newInstance(LostCredentialFragment.Credential.PASSWORD), LostCredentialFragment.TAG)
        }
    }

    private fun subscribeForLiveData() {

        authViewModel.liveLogin.observe(this, Observer {
            renderLoginStatus(it ?: return@Observer)
        })
    }

    private fun renderLoginStatus(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {
            Resource.LOADING -> {
                showLoading(true)
            }
            Resource.FAILURE -> {
                showLoading(false)
                handleError(resource.failure())
            }
            else -> {
                showLoading(false)
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            password.isEnabled = false
            progress_bar.visibility = View.VISIBLE
            submit_button.visibility = View.INVISIBLE
        } else {
            password.isEnabled = true
            progress_bar.visibility = View.GONE
            submit_button.visibility = View.VISIBLE
        }
    }

    /**
     * handling login response errors
     */
    private fun handleError(e: ServerException) {

        when (e.code) {
            ErrorCodes.LOGIN_WRONG_PASSWORD -> {
                password.error = e.message
            }
            else -> {
                showErrorSnackbar(e)
            }
        }
    }

    private fun attemptLogin() {

        val username = authViewModel.liveLastUsername.value ?: return

        authViewModel.login(username, password.trimmedText)
    }

    companion object {
        const val TAG = "PasswordFragment"

        fun newInstance() = PasswordFragment()
    }


}
