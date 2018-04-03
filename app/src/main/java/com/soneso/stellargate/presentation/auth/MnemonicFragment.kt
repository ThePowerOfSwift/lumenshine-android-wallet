package com.soneso.stellargate.presentation.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.soneso.stellargate.R
import kotlinx.android.synthetic.main.fragment_mnemonic.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MnemonicFragment : AuthFragment() {

    private lateinit var mnemonic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mnemonic = arguments?.getString(ARG_MNEMONIC) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mnemonic, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mnemonic_value.text = mnemonic
    }

    companion object {
        const val TAG = "MnemonicFragment"
        private const val ARG_MNEMONIC = "$TAG.ARG_MNEMONIC"

        fun newInstance(mnemonic: String): MnemonicFragment {
            val instance = MnemonicFragment()
            val args = Bundle()
            args.putString(ARG_MNEMONIC, mnemonic)
            instance.arguments = args
            return instance
        }
    }
}
