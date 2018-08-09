package com.soneso.stellargate.presentation.customViews

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import com.soneso.stellargate.R

open class FormInputView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : EditText(context, attrs, defStyleAttr) {

    protected var inputLevel = 0
    private var errorText: CharSequence = ""
    private var regexToMatch = ""

    var trimmedText: CharSequence
        get() = text?.trim() ?: ""
        set(value) {
            text = SpannableStringBuilder(value)
        }

    init {
        applyAttrs(attrs)
        maxLines = 1

        setupInputLevel()
    }


    private fun applyAttrs(attrs: AttributeSet?) {
        val attributeSet = attrs ?: return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FormInputView)
        inputLevel = typedArray.getInt(R.styleable.FormInputView_input_level, 0)
        regexToMatch = typedArray.getString(R.styleable.FormInputView_regex) ?: ""
        errorText = typedArray.getString(R.styleable.FormInputView_error_text) ?: resources.getText(R.string.invalid)
        typedArray.recycle()
    }

    private fun setupInputLevel() {
        if (inputLevel == resources.getInteger(R.integer.input_undesired)) {
            visibility = View.GONE
        }
    }

    fun hasValidInput(): Boolean {
        when {
            inputLevel == resources.getInteger(R.integer.input_mandatory) && text.isNullOrBlank() -> {
                error = resources.getText(R.string.error_field_required)
                return false
            }
            inputLevel == resources.getInteger(R.integer.input_optional) && text.isNullOrBlank() -> {
                return true
            }

            regexToMatch.isNotEmpty() && !trimmedText.matches(Regex(regexToMatch)) -> {
                error = errorText

            }
        }
        return true
    }
}