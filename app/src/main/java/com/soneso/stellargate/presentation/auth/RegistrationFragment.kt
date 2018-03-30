package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.DataStatus
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : AuthFragment() {

    @Inject
    lateinit var regViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_in_button.setOnClickListener { replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG) }

        email_registration_button.setOnClickListener {
            attemptRegistration()
        }
    }

    private fun attemptRegistration() {
        val data = regViewModel.createAccount(email.text, password.text)
        data.liveStatus.observe(this, Observer {
            val status = it ?: return@Observer
            when (status) {
                DataStatus.ERROR -> {
                    Toast.makeText(context, data.errorMessage, Toast.LENGTH_SHORT).show()
                }
                DataStatus.LOADING -> {
                    Log.d(TAG, "Loading registration.")
                }
                DataStatus.SUCCESS -> {
                    Log.d(TAG, "Success at registration step.")
                }
            }
        })
    }

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
