package com.soneso.stellargate.presentation.customViews

import android.content.Context
import android.util.AttributeSet
import com.soneso.stellargate.R

class PasswordInputView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : FormInputView(context, attrs, defStyleAttr) {

    private var minPasswordLength = 9

    fun isValidPassword(): Boolean {
        when {
            inputLevel == resources.getInteger(R.integer.input_mandatory) && text.isNullOrBlank() -> {
                error = resources.getText(R.string.error_field_required)
                return false
            }

            !trimmedText.matches(Regex(".*[A-Z].*")) -> {
                error = resources.getText(R.string.error_invalid_password_min_one_upper_case_char)
                return false
            }

            !trimmedText.matches(Regex(".*[a-z].*")) -> {
                error = resources.getText(R.string.error_invalid_password_min_one_lower_case_char)
                return false
            }

            !trimmedText.matches(Regex(".*\\d.*")) -> {
                error = resources.getText(R.string.error_invalid_password_min_one_digit)
                return false
            }

            trimmedText.length < minPasswordLength -> {
                error = resources.getText(R.string.error_invalid_password_min_nine_characters)
                return false
            }
        }
        return true
    }
}