package com.soneso.lumenshine.presentation.wallets


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.wallet.Wallet
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.general.SgFragment
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_wallets.*


/**
 * A simple [Fragment] subclass.
 */
class WalletsFragment : SgFragment() {

    private lateinit var walletsViewModel: WalletsViewModel
    private lateinit var walletAdapter: WalletAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        walletsViewModel = ViewModelProviders.of(this, viewModelFactory)[WalletsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_wallets, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeForLiveData()
    }

    private fun subscribeForLiveData() {

        walletsViewModel.liveWallets.observe(this, Observer {

            renderWallets(it ?: return@Observer)
        })
    }

    private fun renderWallets(resource: Resource<List<Wallet>, ServerException>) {

        when (resource.state) {
            Resource.LOADING -> {

            }
            Resource.SUCCESS -> {
                walletAdapter.setWalletData(resource.success())
            }
            Resource.FAILURE -> {

            }
        }
    }

    private fun setupRecyclerView() {

        walletAdapter = WalletAdapter()
        walletRecyclerView.layoutManager = LinearLayoutManager(context)
        walletRecyclerView.setHasFixedSize(true)
        walletRecyclerView.adapter = walletAdapter
    }

    companion object {
        const val TAG = "WalletsFragment"

        fun newInstance() = WalletsFragment()
    }
}
