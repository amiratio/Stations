package com.application.stations.ui.activity

import android.os.Bundle
import com.application.stations.presenter.HomePresenter
import com.application.stations.R
import com.application.stations.contract.HomeContract
import com.application.stations.core.BaseActivity
import com.application.stations.databinding.ActivityHomeBinding
import com.application.stations.utils.MapHelper
import com.application.stations.utils.extension.hide
import com.application.stations.utils.extension.show
import com.application.stations.utils.extension.transparentStatusBar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Home : BaseActivity<ActivityHomeBinding>(), OnMapReadyCallback, HomeContract.View {

    @Inject
    lateinit var mapHelper: MapHelper

    @Inject
    lateinit var presenter: HomePresenter

    override val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()

        mapSetup()
        uiSetup()

        presenter.onAttach(this)
    }

    private fun uiSetup() {
        binding.loading.root.setOnClickListener { }
    }

    private fun mapSetup() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@Home)
    }

    override fun onMapReady(gm: GoogleMap) {
        googleMap = gm
        mapHelper.setup(this, googleMap, this)
        presenter.initialize()
    }

    override fun showLoading() {
        runOnUiThread {
            binding.loading.root.show()
        }
    }

    override fun hideLoading() {
        runOnUiThread {
            binding.loading.root.hide()
        }
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }
}