package com.soneso.lumenshine.presentation.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import com.soneso.lumenshine.R

open class SgEditText @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : EditText(context, attrs, defStyleAttr) {
    init {
        applyAttrs(attrs)
    }


    private fun applyAttrs(attrs: AttributeSet?) {
        val attributeSet = attrs ?: return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SgEditText)
        val bordered = typedArray.getBoolean(R.styleable.SgEditText_bordered, false)
        if (bordered) {
            background = resources.getDrawable(R.drawable.edittext_border_normal)
        } else {

        }
        typedArray.recycle()
    }

    fun showError(show: Boolean) {
        background = if (show) {
            resources.getDrawable(R.drawable.edittext_border_error)
        } else {
            resources.getDrawable(R.drawable.edittext_border_normal)
        }
    }
}