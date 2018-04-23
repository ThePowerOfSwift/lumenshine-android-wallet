package com.soneso.stellargate.presentation.general

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import com.soneso.stellargate.R
import com.soneso.stellargate.di.AppComponent
import javax.inject.Inject

/**
 * Base Fragment for Stellargate App.
 * Created by cristi.paval on 3/8/18.
 */
open class SgFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: SgViewModelFactory

    val sgActivity: SgActivity
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
}