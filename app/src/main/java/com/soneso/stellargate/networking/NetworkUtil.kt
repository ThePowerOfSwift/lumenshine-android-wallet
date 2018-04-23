package com.soneso.stellargate.networking

import android.util.Log

object NetworkUtil {

    const val TAG = "NetworkUtil"

    fun isNetworkAvailable(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
        }

        return false
    }
}