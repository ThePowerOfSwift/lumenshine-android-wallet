package com.soneso.stellargate.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.soneso.stellargate.R
import com.soneso.stellargate.model.BlogPostPreview
import com.soneso.stellargate.model.InternalLink
import com.soneso.stellargate.model.Mock
import kotlinx.android.synthetic.main.item_home_internal_link.view.*
import kotlinx.android.synthetic.main.item_home_web_link.view.*

/**
 * Adapter.
 * Created by cristi.paval on 3/8/18.
 */
class HomeFeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onBlogLinkClickListener: ((BlogPostPreview) -> Unit)? = null

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
            TYPE_LOAD_MORE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_load_more, parent, false)
                LoadMoreHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed, parent, false)
                CardViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            1 -> TYPE_WEB
            3 -> TYPE_INTERNAL_LINK
            itemCount - 1 -> TYPE_LOAD_MORE
            else -> TYPE_DEFAULT
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
            else -> {

            }
        }
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class LoadMoreHolder(view: View) : RecyclerView.ViewHolder(view)

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
//        private val readMoreButton = view.help_button

        fun fillData(link: InternalLink) {
            imageView.setImageResource(link.iconResId)
            titleView.text = link.title
            paragraphView.text = link.description
//            readMoreButton.setOnClickListener {
//                onBlogLinkClickListener?.invoke(blog)
//            }
        }
    }

    companion object {
        private const val TYPE_DEFAULT = 0
        private const val TYPE_WEB = 1
        private const val TYPE_INTERNAL_LINK = 2
        private const val TYPE_LOAD_MORE = 3
    }
}