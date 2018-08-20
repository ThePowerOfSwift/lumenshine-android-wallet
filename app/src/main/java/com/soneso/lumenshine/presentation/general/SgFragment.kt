package com.soneso.lumenshine.presentation.general

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.SgError
import javax.inject.Inject

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

        viewModelFactory = SgViewModelFactory(sgActivity.sgApp.appComponent)
    }

    fun showSnackbar(text: CharSequence) {
        val view = view ?: return
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }

    fun showErrorSnackbar(e: SgError?) {
        val error = e ?: return
        val view = view ?: return
        val snackbar = if (error.errorResId > 0) Snackbar.make(view, error.errorResId, Snackbar.LENGTH_LONG) else Snackbar.make(view, error.message!!, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.ok, null)
                .show()
    }
}