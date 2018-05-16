package com.soneso.stellargate.presentation.util

import android.animation.ObjectAnimator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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

fun TextView.setOnTextChangeListener(listener: ((View) -> Unit)) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.invoke(this@setOnTextChangeListener)
        }
    })
}