package com.soneso.stellargate.presentation.general

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import com.soneso.stellargate.R
import com.soneso.stellargate.di.AppComponent
import com.soneso.stellargate.domain.data.SgError
import javax.inject.Inject

/**
 * Base Fragment for Stellargate App.
 * Created by cristi.paval on 3/8/18.
 */
open class SgFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: SgViewModelFactory

    private val sgActivity: SgActivity
        get() = activity as SgActivity

    val appComponent: AppComponent
        get() = sgActivity.appComponent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent.inject(this)
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