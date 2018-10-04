package com.soneso.lumenshine.presentation.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.soneso.lumenshine.R
import kotlinx.android.synthetic.main.dialog_info.view.*


/**
 * general info dialog.
 * Created by attila.janosi on 7/10/18.
 */

private val ICON_RESOURCE = "icon_resource"
private val TITLE_RESOURCE = "title_resource"

class InfoDialog : DialogFragment() {

    private var viewBuilder: ViewBuilder? = null
    private var iconId = 0
    private var titleId = 0

    fun setViewBuilder(viewBuilder: ViewBuilder): InfoDialog {
        this.viewBuilder = viewBuilder
        return this
    }

    override fun onResume() {
        // Get existing layout params for the window
        val params = dialog.window!!.attributes
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        // Call super onResume after sizing
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        iconId = arguments?.getInt(ICON_RESOURCE) ?: 0
        titleId = arguments?.getInt(TITLE_RESOURCE) ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_info, null)
        val dialog = super.onCreateDialog(savedInstanceState)

        if (iconId != 0) {
            mView.info_dialog_title_icon.visibility = View.VISIBLE
            mView.info_dialog_title_icon.setImageResource(iconId)
        } else {
            mView.info_dialog_title_icon.visibility = View.GONE
        }
        mView.info_dialog_title_text.setText(titleId)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val content = viewBuilder!!.createView(activity!!, LayoutInflater.from(activity))
        mView.container.addView(content)

        dialog.setContentView(mView)

        mView.info_dialog_close_button.setOnClickListener {
            dismiss()
        }
        return dialog
    }

    interface ViewBuilder {
        fun createView(context: Context, inflater: LayoutInflater): View
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param titleResource Dialog title resource id.
         * @param iconResource Dialog icon resource id default 0, means that it is hidden.
         * @return A new instance of fragment InfoDialog.
         */
        @JvmStatic
        fun newInstance(titleResource: Int, iconResource: Int = 0) =
                InfoDialog().apply {
                    arguments = Bundle().apply {
                        putInt(TITLE_RESOURCE, titleResource)
                        putInt(ICON_RESOURCE, iconResource)
                    }
                }

        const val TAG = "InfoDialog"
    }

}