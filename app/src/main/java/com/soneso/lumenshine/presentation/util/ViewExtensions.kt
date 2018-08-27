package com.soneso.lumenshine.presentation.util

import android.animation.ObjectAnimator
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Build
import android.support.annotation.StyleRes
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView

/**
 * Extensions.
 * Created by cristi.paval on 3/20/18.
 */

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

fun TextView.setTextStyle(@StyleRes resId: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        @Suppress("DEPRECATION")
        setTextAppearance(context, resId)
    }
}

fun <T> LiveData<T>.putValue(value: T) {
    (this as MutableLiveData).value = value
}