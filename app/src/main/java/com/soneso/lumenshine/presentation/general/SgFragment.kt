package com.soneso.lumenshine.presentation.general

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import com.soneso.lumenshine.R
import com.soneso.lumenshine.util.LsException

/**
 * Base Fragment for Lumenshine App.
 * Created by cristi.paval on 3/8/18.
 */
open class SgFragment : Fragment() {

    lateinit var viewModelFactory: SgViewModelFactory

    private val sgActivity: SgActivity
        get() = activity as SgActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = SgViewModelFactory(sgActivity.lsApp.appComponent)
    }

    fun showSnackbar(text: CharSequence) {
        val view = view ?: return
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }

    fun showErrorSnackbar(e: LsException?) {

        val view = view ?: return
        val message = e?.throwable?.message ?: getString(R.string.unknown_error)
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }
}