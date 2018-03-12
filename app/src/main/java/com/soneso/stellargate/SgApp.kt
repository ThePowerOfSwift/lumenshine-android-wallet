package com.soneso.stellargate

import android.support.multidex.MultiDexApplication
import com.soneso.stellargate.di.AppModule
import com.soneso.stellargate.di.DaggerAppComponent

/**
 * Custom App class.
 * Created by cristi.paval on 3/8/18.
 */
class SgApp : MultiDexApplication() {

    val appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()!!
}