package com.soneso.lumenshine.presentation.customViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.soneso.lumenshine.R

open class LsCardView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : androidx.cardview.widget.CardView(context, attrs, defStyleAttr) {

    init {
        radius = resources.getDimension(R.dimen.size_5)
        cardElevation = resources.getDimension(R.dimen.size_3)
        preventCornerOverlap = false
        useCompatPadding = true
        minimumWidth = resources.getDimensionPixelSize(R.dimen.size_infinite)
        @Suppress("LeakingThis")
        setCardBackgroundColor(Color.WHITE)
    }
}