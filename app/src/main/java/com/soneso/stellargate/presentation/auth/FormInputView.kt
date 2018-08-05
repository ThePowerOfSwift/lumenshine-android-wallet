package com.soneso.stellargate.presentation.auth

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.soneso.stellargate.R
import kotlinx.android.synthetic.main.sg_input_view.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.soneso.stellargate.presentation.util.setOnTextChangeListener


class FormInputView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var inputLevel = 0
    private var errorText: CharSequence = ""
    private var minPasswordLenght = 9
    private var regexToMatch = ""

    var trimmedText: CharSequence
        get() = input_edit_text.text?.trim() ?: ""
        set(value) {
            input_edit_text.text = SpannableStringBuilder(value)
        }

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.sg_input_view, this, true)
        applyAttrs(attrs)
        input_edit_text.maxLines = 1

        setupInputLevel()
        input_edit_text.setOnTextChangeListener { error_text.text = "" }
    }


    private fun applyAttrs(attrs: AttributeSet?) {
        val attributeSet = attrs ?: return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FormInputView)
        inputLevel = typedArray.getInt(R.styleable.FormInputView_input_level, 0)
        regexToMatch = typedArray.getString(R.styleable.FormInputView_regex) ?: ""
        errorText = typedArray.getString(R.styleable.FormInputView_error_text) ?: resources.getText(R.string.invalid)
        val inputType = typedArray.getInt(R.styleable.FormInputView_android_inputType, EditorInfo.TYPE_NULL)
        if (inputType != EditorInfo.TYPE_NULL) {
            input_edit_text.inputType = inputType
        }
        val hint = typedArray.getString(R.styleable.FormInputView_android_hint)
        input_edit_text.hint = hint

        input_edit_text.imeOptions = typedArray.getInt(R.styleable.FormInputView_android_imeOptions, EditorInfo.IME_ACTION_UNSPECIFIED)

        val imeActionId = typedArray.getInt(R.styleable.FormInputView_android_imeActionId, 0)
        input_edit_text.setImeActionLabel(typedArray.getString(R.styleable.FormInputView_android_imeActionLabel), imeActionId)
        typedArray.recycle()
    }

    private fun setupInputLevel() {
        if (inputLevel == resources.getInteger(R.integer.input_undesired)) {
            visibility = View.GONE
        }
    }

    fun hasValidInput(): Boolean {
        when {
            inputLevel == resources.getInteger(R.integer.input_mandatory) && input_edit_text.text.isNullOrBlank() -> {
                error_text.text = resources.getText(R.string.error_field_required)
                return false
            }
            inputLevel == resources.getInteger(R.integer.input_optional) && input_edit_text.text.isNullOrBlank() -> {
                return true
            }

            regexToMatch.isNotEmpty() && !trimmedText.matches(Regex(regexToMatch)) -> {
                error_text.text = errorText
            }
        }
        return true
    }

    fun isValidPassword(): Boolean {
        when {
            inputLevel == resources.getInteger(R.integer.input_mandatory) && input_edit_text.text.isNullOrBlank() -> {
                error_text.text = resources.getText(R.string.error_field_required)
                return false
            }

            !trimmedText.matches(Regex(".*[A-Z].*")) -> {
                error_text.text = resources.getText(R.string.error_invalid_password_min_one_upper_case_char)
                return false
            }

            !trimmedText.matches(Regex(".*[a-z].*")) -> {
                error_text.text = resources.getText(R.string.error_invalid_password_min_one_lower_case_char)
                return false
            }

            !trimmedText.matches(Regex(".*\\d.*")) -> {
                error_text.text = resources.getText(R.string.error_invalid_password_min_one_digit)
                return false
            }

            trimmedText.length < minPasswordLenght -> {
                error_text.text = resources.getText(R.string.error_invalid_password_min_nine_characters)
                return false
            }
        }
        return true
    }

    fun setOnEditorActionListener(listener: TextView.OnEditorActionListener) {
        input_edit_text.setOnEditorActionListener(listener)
    }

    var error: String
        get() = error_text.text.toString()
        set(value) {
            error_text.text = value
        }

}