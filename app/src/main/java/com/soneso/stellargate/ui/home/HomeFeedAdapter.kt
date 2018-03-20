package com.soneso.stellargate.ui.home

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.soneso.stellargate.R
import com.soneso.stellargate.model.BlogPostPreview
import com.soneso.stellargate.model.InternalLink
import com.soneso.stellargate.model.Mock
import com.soneso.stellargate.model.StellarAccount
import com.soneso.stellargate.ui.util.displayQrCode
import kotlinx.android.synthetic.main.item_home_account.view.*
import kotlinx.android.synthetic.main.item_home_chart.view.*
import kotlinx.android.synthetic.main.item_home_internal_link.view.*
import kotlinx.android.synthetic.main.item_home_web_link.view.*

/**
 * Adapter.
 * Created by cristi.paval on 3/8/18.
 */
class HomeFeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onBlogLinkClickListener: ((BlogPostPreview) -> Unit)? = null

    private var account: StellarAccount? = null

    fun setAccount(account: StellarAccount?) {
        this.account = account
        notifyItemChanged(ACCOUNT_POSITION)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WEB -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_web_link, parent, false)
                BlogPostHolder(view)
            }
            TYPE_INTERNAL_LINK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_internal_link, parent, false)
                InternalLinkHolder(view)
            }
            TYPE_ACCOUNT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_account, parent, false)
                AccountHolder(view)
            }
            TYPE_CHART -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_chart, parent, false)
                ChartHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_load_more, parent, false)
                LoadMoreHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            ACCOUNT_POSITION -> TYPE_ACCOUNT
            1 -> TYPE_CHART
            2 -> TYPE_WEB
            3 -> TYPE_INTERNAL_LINK
            else -> TYPE_LOAD_MORE
        }
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_WEB -> {
                (holder as BlogPostHolder).fillData(Mock.mockBlogPost())
            }
            TYPE_INTERNAL_LINK -> {
                (holder as InternalLinkHolder).fillData(Mock.mockInternalLink())
            }
            TYPE_CHART -> {
                (holder as ChartHolder).fillData()
            }
            TYPE_ACCOUNT -> {
                (holder as AccountHolder).fillData(account ?: return)
            }
            else -> {

            }
        }
    }

    inner class AccountHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.account_image

        fun fillData(account: StellarAccount) {
            imageView.displayQrCode(account.accountId)

        }
    }

    inner class LoadMoreHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class ChartHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val chartView = view.line_chart

        fun fillData() {
            chartView.legend.isEnabled = false
            chartView.description.isEnabled = false
            chartView.axisLeft.setDrawLabels(false)
            chartView.axisLeft.gridColor = ContextCompat.getColor(chartView.context, R.color.cyan_300)
            chartView.axisLeft.axisLineColor = Color.TRANSPARENT
            chartView.axisRight.isEnabled = false
            chartView.xAxis.setDrawLabels(false)
            chartView.xAxis.setDrawGridLines(false)
            chartView.xAxis.axisLineColor = Color.TRANSPARENT
            val dataSet = LineDataSet(listOf(
                    Entry(0f, 0.5f),
                    Entry(1f, 0.5f),
                    Entry(2f, 0.55f),
                    Entry(3f, 0.6f),
                    Entry(4f, 0.625f)
            ), null)
            dataSet.color = Color.WHITE
            dataSet.lineWidth = chartView.resources.getDimension(R.dimen.size_05)
            dataSet.circleRadius = chartView.resources.getDimension(R.dimen.size_1_5)
            dataSet.valueTextSize = 8f
            val data = LineData(dataSet)
            data.setValueTextColor(Color.WHITE)
            chartView.data = data
        }
    }

    inner class BlogPostHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.blog_image
        private val titleView = view.blog_title
        private val subtitleView = view.blog_subtitle
        private val paragraphView = view.blog_paragraph
        private val readMoreButton = view.blog_button

        fun fillData(blog: BlogPostPreview) {
            Glide.with(itemView.context)
                    .load(blog.imageUrl)
                    .into(imageView)
            titleView.text = blog.title
            subtitleView.text = blog.subtitle
            paragraphView.text = blog.paragraph
            readMoreButton.setOnClickListener {
                onBlogLinkClickListener?.invoke(blog)
            }
        }
    }

    inner class InternalLinkHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.card_image
        private val titleView = view.card_title
        private val paragraphView = view.card_paragraph

        fun fillData(link: InternalLink) {
            imageView.setImageResource(link.iconResId)
            titleView.text = link.title
            paragraphView.text = link.description
        }
    }

    companion object {
        private const val TYPE_ACCOUNT = 0
        private const val TYPE_WEB = 1
        private const val TYPE_INTERNAL_LINK = 2
        private const val TYPE_LOAD_MORE = 3
        private const val TYPE_CHART = 4
        private const val ACCOUNT_POSITION = 0
    }
}