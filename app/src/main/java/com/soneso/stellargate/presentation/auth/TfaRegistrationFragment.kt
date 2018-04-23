package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.DataStatus
import com.soneso.stellargate.presentation.util.displayQrCode
import kotlinx.android.synthetic.main.fragment_tfa_registration.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TfaRegistrationFragment : AuthFragment() {

    private lateinit var regViewModel: RegistrationViewModel

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        token = arguments?.getString(ARG_TOKEN) ?: ""

        regViewModel = ViewModelProviders.of(authActivity, viewModelFactory)[RegistrationViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_tfa_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupQrCode()
        setupToken()
        setupTfaCode()
    }

    private fun setupTfaCode() {
        send_button.setOnClickListener {
            val dataProvider = regViewModel.confirmTfaRegistration(tfa_code_view.text.toString())
            dataProvider.liveStatus.observe(this, Observer {
                val status = it ?: return@Observer

                when (status) {
                    DataStatus.SUCCESS -> {
                        showSnackbar("Successful confirmation!")
                    }
                    DataStatus.ERROR -> {
                        showErrorSnackbar(dataProvider.error)
                    }
                    else -> {
                        showSnackbar("Loading...")
                    }
                }
            })
        }
    }

    private fun setupToken() {
        token_view.keyListener = null
        token_view.setText(token)
        copy_button.setOnClickListener {
            val clipboard = context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", token)
            clipboard.primaryClip = clip
            showSnackbar("Token copied in clipboard!")
        }
    }

    private fun setupQrCode() {
        qr_code_view.post {
            val params = qr_code_view.layoutParams
            params.height = qr_code_view.width
            qr_code_view.requestLayout()
            qr_code_view.displayQrCode(token)
        }
    }

    companion object {
        const val TAG = "TfaRegistrationFragment"
        private const val ARG_TOKEN = "$TAG.ARG_TOKEN"

        fun newInstance(token: String): TfaRegistrationFragment {
            val instance = TfaRegistrationFragment()
            val args = Bundle()
            args.putString(ARG_TOKEN, token)
            instance.arguments = args
            return instance
        }
    }
}
