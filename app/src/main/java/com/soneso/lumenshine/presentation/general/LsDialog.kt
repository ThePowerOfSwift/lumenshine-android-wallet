package com.soneso.lumenshine.presentation.general

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.soneso.lumenshine.R

abstract class LsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.window?.apply {
            setBackgroundDrawableResource(R.drawable.bg_top_rounded3_inset)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, if (hasMaxHeight()) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT)
            attributes?.gravity = Gravity.BOTTOM
        }
        return dialog
    }

    open fun hasMaxHeight() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val rootView = getContentLayout(inflater, container, savedInstanceState)
        rootView.minimumWidth = resources.getDimensionPixelSize(R.dimen.size_infinite)
        return rootView
    }

    abstract fun getContentLayout(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
}