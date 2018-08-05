package com.soneso.stellargate.presentation.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.soneso.stellargate.R


private const val FRAGMENT_TYPE = "fragment_type"

/**
 * A simple [Fragment] subclass.
 *
 */
class FingerPrintFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finger_print, container, false)
    }

    companion object {

        const val TAG = "FingerPrintFragment"

        fun newInstance(fragmentType: FingerPrintFragment.FingerprintFragmentType): FingerPrintFragment {

            val instance = FingerPrintFragment()
            val args = Bundle()
            args.putSerializable(FRAGMENT_TYPE, fragmentType)
            instance.arguments = args
            return instance
        }
    }

    enum class FingerprintFragmentType {
        FINGERPRINT, FACE_ID
    }


}
