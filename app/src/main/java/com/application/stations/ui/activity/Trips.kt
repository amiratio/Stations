package com.application.stations.ui.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.application.stations.presenter.HomePresenter
import com.application.stations.R
import com.application.stations.contract.HomeContract
import com.application.stations.core.BaseActivity
import com.application.stations.databinding.ActivityHomeBinding
import com.application.stations.databinding.ActivityTripsBinding
import com.application.stations.remote.Repository
import com.application.stations.ui.adapter.TripsAdapter
import com.application.stations.utils.MapHelper
import com.application.stations.utils.MapHelper.Companion.selectedStation
import com.application.stations.utils.extension.backgroundColorStatusBar
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
class Trips : BaseActivity<ActivityTripsBinding>() {

    @Inject
    lateinit var repository: Repository

    override val binding by lazy { ActivityTripsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundColorStatusBar()

        uiSetup()
    }

    private fun uiSetup() {
        with(binding){
            loading.root.setOnClickListener { }

            rv.adapter= TripsAdapter(selectedStation?.trips?: arrayListOf()){

            }
        }

    }
}