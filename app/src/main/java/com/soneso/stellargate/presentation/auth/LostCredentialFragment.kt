package com.soneso.stellargate.presentation.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R

/**
 * A simple [Fragment] subclass.
 */
class LostCredentialFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_lost_credential, container, false)


    companion object {

        const val TAG = "LostCredentialFragment"
        private const val ARG_CREDENTIAL = "$TAG.ARG_CREDENTIAL"

        fun newInstance(credential: Credential): LostCredentialFragment {

            val instance = LostCredentialFragment()
            val args = Bundle()
            args.putSerializable(ARG_CREDENTIAL, credential)
            instance.arguments = args
            return instance
        }
    }

    enum class Credential {
        PASSWORD, TFA
    }
}
