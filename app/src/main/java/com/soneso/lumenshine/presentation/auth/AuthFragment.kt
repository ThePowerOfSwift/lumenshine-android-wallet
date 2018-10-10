package com.soneso.lumenshine.presentation.auth

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.soneso.lumenshine.R
import com.soneso.lumenshine.presentation.general.LsFragment

/**
 * Fragment.
 * Created by cristi.paval on 3/21/18.
 */
open class AuthFragment : LsFragment() {

    protected val authViewModel: AuthViewModel
        get() = authActivity.authViewModel

    private val authActivity: BaseAuthActivity
        get() = activity as BaseAuthActivity

    fun replaceFragment(fragment: AuthFragment, tag: String) {

//        authActivity.replaceFragment(fragment, tag)
    }

    override fun onDestroyView() {

        hideProgressDialog()
        super.onDestroyView()
    }

    /**
     * Shows progress dialog
     */
    fun showProgressDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context ?: return)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, view as ViewGroup, false)
        builder.setView(view)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.show()
    }

    var dialog: AlertDialog? = null

    /**
     * Hides the progress dialog
     */
    fun hideProgressDialog() {
        dialog?.dismiss()
        dialog = null
    }
}