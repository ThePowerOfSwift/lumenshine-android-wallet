package com.soneso.stellargate.ui.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R
import com.soneso.stellargate.networking.RequestManager
import com.soneso.stellargate.persistence.SgPrefs
import com.soneso.stellargate.ui.SgFragment
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : SgFragment() {

    @Inject
    lateinit var sgPrefs: SgPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFeedRecyclerView()
    }

    private fun setupFeedRecyclerView() {
        rv_feed.layoutManager = LinearLayoutManager(context)
        rv_feed.adapter = HomeFeedAdapter()
    }

    override fun onStart() {
        super.onStart()

        RequestManager().createAccount(sgPrefs.accountId())
    }

    companion object {
        const val TAG = "HomeFragment"

        fun newInstance() = HomeFragment()
    }
}
