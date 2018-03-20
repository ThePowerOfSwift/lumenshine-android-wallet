package com.soneso.stellargate.presentation

import android.support.v4.app.Fragment
import com.soneso.stellargate.di.AppComponent

/**
 * Base Fragment for Stellargate App.
 * Created by cristi.paval on 3/8/18.
 */
open class SgFragment : Fragment() {

    val sgActivity: SgActivity
        get() = activity as SgActivity

    val appComponent: AppComponent
        get() = sgActivity.sgApp.appComponent
}