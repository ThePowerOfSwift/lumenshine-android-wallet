package com.soneso.stellargate.presentation.general

import android.os.Bundle
import android.support.v4.app.Fragment
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
}