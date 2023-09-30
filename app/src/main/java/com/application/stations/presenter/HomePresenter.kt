package com.application.stations.presenter

import com.application.stations.contract.HomeContract
import com.application.stations.utils.MapHelper
import javax.inject.Inject

class HomePresenter @Inject constructor(private val mapHelper: MapHelper) : HomeContract.Presenter {

    private var view: HomeContract.View? = null

    override fun onAttach(view: HomeContract.View) {
        this.view = view
    }

    override fun onDetach() {
        view = null
    }

    override fun initialize() {
        mapHelper.limitToTurkey()
        mapHelper.fetchCurrentLocation()
    }

    override fun gotoMyLocation() {
        mapHelper.gotoMyLocation()
    }

    override fun showAllStations() {
        mapHelper.showAll()
    }

    override fun showNearbyStations() {
        mapHelper.showNearby()
    }
}