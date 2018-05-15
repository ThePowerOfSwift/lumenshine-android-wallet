package com.soneso.stellargate.presentation.home


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.general.SgFragment
import com.soneso.stellargate.presentation.util.forwardToBrowser
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : SgFragment() {

    lateinit var homeViewModel: HomeViewModel

    lateinit var adapter: HomeFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModel = ViewModelProviders.of(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFeedRecyclerView()

        homeViewModel.liveAccountDetails.observe(this, Observer {
            adapter.setAccount(it)
        })
    }

    private fun setupFeedRecyclerView() {
        rv_feed.layoutManager = LinearLayoutManager(context)
        adapter = HomeFeedAdapter()
        rv_feed.adapter = adapter
        adapter.onBlogLinkClickListener = {
            context?.forwardToBrowser(it.postUrl)
        }
    }

    companion object {
        const val TAG = "HomeFragment"

        fun newInstance() = HomeFragment()
    }
}
