package com.soneso.stellargate.presentation.util

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build

/**
 * returns that the current device supports fingerprint reading or not
 */
fun Context.hasFingerPrintSensor(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val fingerprintManager = this.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        if (!fingerprintManager.isHardwareDetected) {
            false
        } else fingerprintManager.hasEnrolledFingerprints()
    } else {
        false
    }
}