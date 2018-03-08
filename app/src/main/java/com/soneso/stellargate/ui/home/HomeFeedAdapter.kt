package com.soneso.stellargate.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R

/**
 * Adapter.
 * Created by cristi.paval on 3/8/18.
 */
class HomeFeedAdapter : RecyclerView.Adapter<HomeFeedAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)
}