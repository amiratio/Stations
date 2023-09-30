package com.application.stations.ui.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.application.stations.R
import com.application.stations.contract.HomeContract
import com.application.stations.core.BaseActivity
import com.application.stations.databinding.ActivityHomeBinding
import com.application.stations.presenter.HomePresenter
import com.application.stations.ui.activity.Trips.Companion.tripSelected
import com.application.stations.utils.MapHelper
import com.application.stations.utils.MapHelper.Companion.selectedStation
import com.application.stations.utils.extension.delay
import com.application.stations.utils.extension.goto
import com.application.stations.utils.extension.hide
import com.application.stations.utils.extension.invisible
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
    private var doubleBackToExitPressedOnce = false

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.exit_app), Toast.LENGTH_SHORT).show()
        delay(2000){
            doubleBackToExitPressedOnce = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()

        mapSetup()
        uiSetup()

        presenter.onAttach(this)
    }

    private fun uiSetup() {
        with(binding){
            loading.root.setOnClickListener { }

            stationsMode.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) presenter.showAllStations()
                else presenter.showNearbyStations()
            }

            currentLocation.setOnClickListener {
                presenter.gotoMyLocation()
            }

            listTrips.setOnClickListener{
                if(selectedStation != null) goto(Trips())
            }
        }

    }

    private fun mapSetup() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@Home)
    }

    override fun onMarkerSelected() {
        binding.listTrips.apply {
            if(!isVisible){
                show()
                startAnimation(AnimationUtils.loadAnimation(this@Home, R.anim.fade_in))
            }
        }
    }

    override fun onMarkerUnSelected() {
        binding.listTrips.apply {
            if(isVisible){
                startAnimation(AnimationUtils.loadAnimation(this@Home, R.anim.fade_out))
                invisible()
            }
        }
    }

    override fun gotoTripsList() {
        binding.listTrips.performClick()
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

    override fun onResume() {
        super.onResume()
        if(tripSelected) presenter.checkReservation()
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }
}