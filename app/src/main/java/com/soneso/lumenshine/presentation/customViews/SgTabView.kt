package com.soneso.lumenshine.presentation.customViews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Checkable
import android.widget.LinearLayout
import com.soneso.lumenshine.R
import kotlinx.android.synthetic.main.sg_tab_view.view.*

class SgTabView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), Checkable {

    private var checked = false
    private var checkedColor: Int = 0
    private var uncheckedColor: Int = 0

    override fun toggle() {
        checked = !this.checked
    }

    override fun isChecked(): Boolean {
        return this.checked
    }

    override fun setChecked(p0: Boolean) {
        if (p0 != this.checked) {
            this.checked = p0
            refreshDrawableState()
        }
        setColors()
    }

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.sg_tab_view, this, true)

        attrs.let {
            val typedArray = context.obtainStyledAttributes(it,
                    R.styleable.SgTabView, 0, 0)
            val title = resources.getText(typedArray
                    .getResourceId(R.styleable
                            .SgTabView_tab_text,
                            R.string.menu_item_home))
            val iconResourceId = typedArray.getResourceId(R.styleable.SgTabView_tab_icon, R.drawable.ic_action_home)
            tab_icon.setImageResource(iconResourceId)
            checkedColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.SgTabView_tab_checked_color, R.color.colorAccent))
            uncheckedColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.SgTabView_tab_unchecked_color, R.color.text_light))
            this.checked = typedArray.getBoolean(R.styleable.SgTabView_tab_checked, false)

            tab_text.text = title

            typedArray.recycle()
            setColors()
        }
    }


    /**
     * sets color for icon and text for checked state
     */
    private fun setColors() {
        if (this.checked) {
            tab_icon.setColorFilter(checkedColor)
            tab_text.setTextColor(checkedColor)
        } else {
            tab_icon.setColorFilter(uncheckedColor)
            tab_text.setTextColor(uncheckedColor)
        }
    }

}