package com.soneso.lumenshine.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.soneso.lumenshine.R
import com.soneso.lumenshine.presentation.general.DialogWithHeader
import kotlinx.android.synthetic.main.password_requirements.*

class PasswordRequirementsDialog : DialogWithHeader() {
    override fun getTitle(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val requirementAdapter = PasswordRequirementAdapter()

    override fun getContentLayout(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.password_requirements, container, false).apply {
                minimumHeight = resources.displayMetrics.heightPixels

            }

    private var listener: View.OnClickListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passwordRequirementsRecyclerView.layoutManager = LinearLayoutManager(context)
        passwordRequirementsRecyclerView.isNestedScrollingEnabled = true
        passwordRequirementsRecyclerView.adapter = requirementAdapter


        val requirements = ArrayList<String>()
        requirements.add("9 characters")
        requirements.add("one small letter")
        requirements.add("one capital letter")
        requirementAdapter.setPasswordRequirements(requirements)
    }

    companion object {
        const val TAG = "PasswordRequirementsDialog"

        fun showInstance(fm: FragmentManager) {
            PasswordRequirementsDialog().apply {
                listener = View.OnClickListener {
                    dismiss()
                }
            }.show(fm, TAG)
        }
    }
}
