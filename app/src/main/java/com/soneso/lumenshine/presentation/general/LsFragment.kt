package com.soneso.lumenshine.presentation.general

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.soneso.lumenshine.R
import com.soneso.lumenshine.util.LsException

/**
 * Base Fragment for Lumenshine App.
 * Created by cristi.paval on 3/8/18.
 */
open class LsFragment : Fragment() {

    lateinit var viewModelFactory: LsViewModelFactory

    private val lsActivity: LsActivity
        get() = activity as LsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = LsViewModelFactory(lsActivity.lsApp.appComponent)
    }

    fun showSnackbar(text: CharSequence) {
        val view = view ?: return
        Snackbar.make(view, text, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }

    fun showSnackbar(@StringRes textId: Int) {
        val view = view ?: return
        Snackbar.make(view, textId, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }

    fun showErrorSnackbar(e: LsException?) {

        val view = view ?: return
        val message = e?.throwable?.message ?: getString(R.string.unknown_error)
        com.google.android.material.snackbar.Snackbar.make(view, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }
}