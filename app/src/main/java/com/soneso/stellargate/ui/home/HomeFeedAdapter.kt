package com.soneso.stellargate.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.soneso.stellargate.R
import com.soneso.stellargate.model.BlogPostPreview
import com.soneso.stellargate.model.Mock
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
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_feed, parent, false)
                CardViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            1 -> TYPE_WEB
            else -> TYPE_DEFAULT
        }
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_WEB -> {
                (holder as BlogPostHolder).fillData(Mock.mockBlogPost())
            }
            else -> {

            }
        }
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)

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

    companion object {
        private const val TYPE_DEFAULT = 0
        private const val TYPE_WEB = 1
    }
}