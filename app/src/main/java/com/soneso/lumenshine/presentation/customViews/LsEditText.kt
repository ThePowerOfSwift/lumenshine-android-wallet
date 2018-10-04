package com.soneso.lumenshine.presentation.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import com.soneso.lumenshine.R

open class LsEditText @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : EditText(context, attrs, defStyleAttr) {
    init {
        applyAttrs(attrs)
    }


    private fun applyAttrs(attrs: AttributeSet?) {
        val attributeSet = attrs ?: return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LsEditText)
        val bordered = typedArray.getBoolean(R.styleable.LsEditText_bordered, false)
        if (bordered) {
            setBackgroundResource(R.drawable.edittext_border_normal)
        }
        typedArray.recycle()
    }

    fun showError(show: Boolean) {
        setBackgroundResource(if (show) R.drawable.edittext_border_error else R.drawable.edittext_border_normal)
    }
}