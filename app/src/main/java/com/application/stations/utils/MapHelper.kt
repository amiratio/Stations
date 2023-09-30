package com.application.stations.utils

import android.Manifest
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.application.stations.R
import com.application.stations.contract.MapHelperContract
import com.application.stations.model.Station
import com.application.stations.remote.Repository
import com.application.stations.ui.activity.Home
import com.application.stations.utils.extension.displayHeight
import com.application.stations.utils.extension.displayWidth
import com.application.stations.utils.extension.uiThread
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.permissionx.guolindev.PermissionX
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import javax.inject.Inject

class MapHelper @Inject constructor(private val repository: Repository) : MapHelperContract {

    private lateinit var appCompatActivity: AppCompatActivity
    private lateinit var map: GoogleMap
    private var currentLat = 0.0
    private var currentLng = 0.0
    private val stations = ArrayList<Station>()
    private lateinit var home: Home

    override fun setup(appCompatActivity: AppCompatActivity, map: GoogleMap, home: Home) {
        this.appCompatActivity = appCompatActivity
        this.map = map
        this.home = home
    }

    override fun limitToTurkey() {
        val one = LatLng(42.743745, 24.908826)
        val two = LatLng(35.382387, 45.818774)
        val builder = LatLngBounds.Builder().apply {
            include(one)
            include(two)
        }
        val bounds = builder.build()
        val padding = 10
        map.apply {
            setLatLngBoundsForCameraTarget(bounds)
            moveCamera(
                CameraUpdateFactory
                    .newLatLngBounds(bounds, displayWidth(), displayHeight(), padding)
            )
            setMinZoomPreference(map.cameraPosition.zoom)
        }
    }

    override fun fetchCurrentLocation() {
        PermissionX.init(appCompatActivity).permissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).request { allGranted, _, _ ->
            if (allGranted) {
                locationRequest()
                val builder = LocationParams.Builder().setAccuracy(LocationAccuracy.HIGH)
                    .setDistance(0.1f).setInterval(3000)
                val smartLocation = SmartLocation.with(appCompatActivity).location()
                    .config(LocationParams.BEST_EFFORT).continuous().config(builder.build())
                smartLocation.start { location ->
                    currentLat = location.latitude
                    currentLng = location.longitude
                    smartLocation.stop()
                    moveCamera(LatLng(location.latitude, location.longitude))
                    getStations()
                }
            } else {
                Message(appCompatActivity).gpsRequired {
                    if (it) fetchCurrentLocation()
                }
            }
        }
    }

    private fun getStations(){
        home.showLoading()
        map.clear()
        stations.clear()
        repository.stations{
            for(i in it){
                if(!i.center_coordinates.isNullOrBlank()){
                    stations.add(i)
                }
            }
            refreshMap(stations)
            home.hideLoading()
        }
    }

    override fun refreshMap(list: ArrayList<Station>) {
        uiThread {
            map.clear()
        }
        for (i in list) {
            if (!i.center_coordinates.isNullOrBlank()) {
                val coordinate = i.center_coordinates.split(",")
                val latLng = LatLng(coordinate[0].toDouble(), coordinate[1].toDouble())
                createMarker(latLng, i.trips_count ?: 0)
            }
        }
    }

    override fun showNearby() {
        refreshMap(FilterNearbyStations(stations, currentLat, currentLng).filterWithinRadius())
    }

    override fun showAll() {
        refreshMap(stations)
    }

    private fun moveCamera(latLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }

    private fun createMarker(latLng: LatLng, tripsCount: Int) {
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(appCompatActivity.resources, R.drawable.spot),
            70,
            70,
            false
        )
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
        uiThread {
            map.addMarker(MarkerOptions().position(latLng).title(
                "$tripsCount ${appCompatActivity.getString(R.string.trips)}"
            ))!!.setIcon(smallMarkerIcon)
        }
    }

    private fun locationRequest() {
        val mLocationRequest =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong()).setFastestInterval((1 * 1000).toLong())
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(appCompatActivity)
            .checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(appCompatActivity, 3)
                    } catch (_: IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }
}