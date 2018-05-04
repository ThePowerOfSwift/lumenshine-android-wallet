package com.soneso.stellargate.presentation.util

import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.soneso.stellargate.presentation.accounts.AccountsFragment
import java.util.*
import kotlin.math.min

/**
 * Extensions.
 * Created by cristi.paval on 3/20/18.
 */

fun ImageView.displayQrCode(text: String) {
    post {
        val sizeAsPixels = min(height, width)
        scaleType = ImageView.ScaleType.CENTER_INSIDE

        if (text.isBlank()) {
            return@post
        }
        val multiFormatWriter = MultiFormatWriter()
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1

            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, sizeAsPixels, sizeAsPixels, hints)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            setImageBitmap(bitmap)

        } catch (e: WriterException) {
            Log.e(AccountsFragment.TAG, e.javaClass.simpleName, e)
        }
    }
}

fun View.fadeIn() {
    visibility = View.VISIBLE
    val animator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
    animator.duration = 500
    animator.start()
}