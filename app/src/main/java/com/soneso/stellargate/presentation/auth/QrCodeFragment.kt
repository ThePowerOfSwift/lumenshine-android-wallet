package com.soneso.stellargate.presentation.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.util.displayQrCode
import kotlinx.android.synthetic.main.fragment_qr_code.*

/**
 * A simple [Fragment] subclass.
 *
 */
class QrCodeFragment : AuthFragment() {

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        token = arguments?.getString(ARG_TOKEN) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_qr_code, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupQrCode()
        setupToken()
    }

    private fun setupToken() {
        token_view.setText(token)
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
        const val TAG = "QrCodeFragment"
        private const val ARG_TOKEN = "$TAG.ARG_TOKEN"

        fun newInstance(token: String): QrCodeFragment {
            val instance = QrCodeFragment()
            val args = Bundle()
            args.putString(ARG_TOKEN, token)
            instance.arguments = args
            return instance
        }
    }
}
