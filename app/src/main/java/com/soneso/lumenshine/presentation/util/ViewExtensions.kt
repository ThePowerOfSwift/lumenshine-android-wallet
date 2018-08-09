package com.soneso.lumenshine.presentation.util

import android.animation.ObjectAnimator
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