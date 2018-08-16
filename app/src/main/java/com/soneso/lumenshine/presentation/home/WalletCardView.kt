package com.soneso.lumenshine.presentation.home

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.soneso.lumenshine.R

class WalletCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.style.LsCardView
) : CardView(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_wallet_card, this)
    }
}