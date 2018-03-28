package com.soneso.stellargate.domain.util

import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.BlogPostPreview
import com.soneso.stellargate.domain.data.InternalLink

/**
 * Mocker class.
 * Created by cristi.paval on 3/16/18.
 */
class Mock {

    companion object {

        fun mockBlogPost(): BlogPostPreview {
            val post = BlogPostPreview()
            post.imageUrl = "https://keybase.io/images/blog/series_a/kbcloud.jpg"
            post.title = "Keybase and Stellar: Partnership Announcement"
            post.subtitle = "Christian, March 8, 2018"
            post.paragraph = "We’re very pleased to announce that Stellar is working with Keybase through a special instance of SDF’s partnership grant program. Keybase has created a general set of cryptographically secure tools (chat, file storage, git) and we encourage you to check them out."
            post.postUrl = "https://www.stellar.org/blog/keybase-and-stellar-partnership/"
            return post
        }

        fun mockInternalLink(): InternalLink {
            val link = InternalLink()
            link.iconResId = R.drawable.mock_help_feedback
            link.title = "Help & Feedback"
            link.description = "Need help or have suggestion?\n\nThe Stellargate support team is just a quick tap away. Take a look at our FAQ or send us a message."
            return link
        }
    }
}