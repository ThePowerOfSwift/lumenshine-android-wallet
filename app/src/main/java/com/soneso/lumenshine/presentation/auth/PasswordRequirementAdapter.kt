package com.soneso.lumenshine.presentation.auth;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soneso.lumenshine.R
import kotlinx.android.synthetic.main.password_requirement_item.view.*
import java.util.zip.Inflater

public class PasswordRequirementAdapter: RecyclerView.Adapter<PasswordRequirementAdapter.RequirementViewHolder>() {
    override fun getItemCount(): Int {
        return requirements.size
    }

    override fun onBindViewHolder(holder: RequirementViewHolder, position: Int) {
        holder.bindData()
    }

    private val requirements = ArrayList<String>()

    fun setPasswordRequirements(requirements: ArrayList<String>) {
        this.requirements.clear()
        this.requirements.addAll(requirements)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rootView = inflater.inflate(R.layout.password_requirement_item, parent, false)
        return RequirementViewHolder(rootView)
    }

    inner class RequirementViewHolder(v:View) :RecyclerView.ViewHolder(v) {
        private val requirementTitle = v.passwordRequirementTitle
        private val requirementSubtitle = v.passwordRequirementSubtitle

        fun bindData() {
            val data = requirements[adapterPosition]
            requirementTitle.text = data
            requirementSubtitle.text = data
        }
    }
}
