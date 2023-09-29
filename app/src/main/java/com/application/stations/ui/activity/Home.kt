package com.application.stations.ui.activity

import android.os.Bundle
import com.application.stations.R
import com.application.stations.core.BaseActivity
import com.application.stations.databinding.ActivityHomeBinding
import com.application.stations.utils.MapHelper
import com.application.stations.utils.extension.transparentStatusBar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class Home : BaseActivity<ActivityHomeBinding>(), OnMapReadyCallback {

    override val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private val mapHelper by lazy { MapHelper(googleMap) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()

        mapSetup()
    }

    private fun mapSetup(){
        mapFragment= supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@Home)
    }

    override fun onMapReady(gm: GoogleMap) {
        googleMap= gm
        mapHelper.limitToTurkey()
    }
}