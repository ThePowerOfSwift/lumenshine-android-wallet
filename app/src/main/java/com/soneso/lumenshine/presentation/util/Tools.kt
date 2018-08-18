package com.soneso.lumenshine.presentation.util

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import com.google.common.io.BaseEncoding
import java.nio.charset.Charset

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

fun String.decodeBase32(): String {
    val decoded = BaseEncoding.base32().decode(this)
    return String(decoded, Charset.forName("UTF-8"))
}