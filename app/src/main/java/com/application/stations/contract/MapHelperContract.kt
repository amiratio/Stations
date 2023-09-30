package com.application.stations.contract

import androidx.appcompat.app.AppCompatActivity
import com.application.stations.model.Station
import com.application.stations.ui.activity.Home
import com.google.android.gms.maps.GoogleMap

interface MapHelperContract {

    fun setup(appCompatActivity: AppCompatActivity, map: GoogleMap, home: Home)

    fun limitToTurkey()

    fun fetchCurrentLocation()

    fun refreshMap()

    fun showNearby()

    fun showAll()

    fun gotoMyLocation()

    fun checkReservation()

}