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
import com.application.stations.utils.Constants.TURKEY_END_POINT
import com.application.stations.utils.Constants.TURKEY_START_POINT
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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
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
    private var showingStations = ArrayList<Station>()
    private lateinit var home: Home
    private var mode= MapMode.NEARBY

    companion object{
        val stations = ArrayList<Station>()
        var selectedStation: Station?= null
    }

    override fun setup(appCompatActivity: AppCompatActivity, map: GoogleMap, home: Home) {
        this.appCompatActivity = appCompatActivity
        this.map = map
        this.home = home

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(appCompatActivity, R.raw.map_style))
        markerListener()
    }

    override fun limitToTurkey() {
        val builder = LatLngBounds.Builder().apply {
            include(TURKEY_START_POINT)
            include(TURKEY_END_POINT)
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
                    moveCamera(LatLng(currentLat, currentLng))
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
            if(mode == MapMode.NEARBY) showNearby()
            else showAll()
            home.hideLoading()
        }
    }

    override fun refreshMap() {
        uiThread {
            map.clear()
            createMarker(LatLng(currentLat, currentLng), 0, R.drawable.my_location)
        }
        val selectedStationLatLng= if(selectedStation != null){
            val selectedCoordinate = selectedStation?.center_coordinates?.split(",")!!
            LatLng(selectedCoordinate[0].toDouble(),
                selectedCoordinate[1].toDouble())
        }
        else null
        for (i in showingStations) {
            if (!i.center_coordinates.isNullOrBlank()) {
                val coordinate = i.center_coordinates.split(",")
                val latLng = LatLng(coordinate[0].toDouble(), coordinate[1].toDouble())
                uiThread {
                    if(selectedStation == null){
                        if(i.hasReserve == true){
                            createMarker(latLng,
                                i.trips_count ?: 0, R.drawable.booked_spot
                            )
                        }else{
                            createMarker(latLng,
                                i.trips_count ?: 0, R.drawable.spot
                            )
                        }
                    }else{
                        if(latLng.latitude == selectedStationLatLng?.latitude &&
                            latLng.longitude == selectedStationLatLng.longitude){
                            createMarker(latLng,
                                i.trips_count ?: 0, R.drawable.selected_spot
                            ).showInfoWindow()
                        }else{
                            if(i.hasReserve == true){
                                createMarker(latLng,
                                    i.trips_count ?: 0, R.drawable.booked_spot
                                )
                            }else{
                                createMarker(latLng,
                                    i.trips_count ?: 0, R.drawable.spot
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun showNearby() {
        mode= MapMode.NEARBY
        showingStations= FilterNearbyStations(stations, currentLat, currentLng).filterWithinRadius()
        refreshMap()
    }

    override fun showAll() {
        mode= MapMode.ALL
        showingStations= stations
        refreshMap()
    }

    private fun moveCamera(latLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }

    override fun gotoMyLocation() {
        moveCamera(LatLng(currentLat, currentLng))
    }

    private fun createMarker(latLng: LatLng, tripsCount: Int, drawable: Int): Marker {
        val marker: Marker?
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(appCompatActivity.resources, drawable),
            70,
            70,
            false
        )
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
        if(drawable == R.drawable.my_location){
            marker= map.addMarker(MarkerOptions().position(latLng))!!
            marker.setIcon(smallMarkerIcon)
        }else{
            marker= map.addMarker(MarkerOptions().position(latLng).title(
                "$tripsCount ${appCompatActivity.getString(R.string.trips)}"
            ))!!
            marker.setIcon(smallMarkerIcon)
        }
        return marker
    }

    private fun markerListener(){
        map.setOnMarkerClickListener {
            selectedStation= null
            for(i in showingStations){
                val lat= i.center_coordinates!!.split(",")[0].toDouble()
                val lng= i.center_coordinates.split(",")[1].toDouble()
                if(lat == it.position.latitude && lng == it.position.longitude){
                    selectedStation= i
                    home.onMarkerSelected()
                }
            }
            refreshMap()
            true
        }

        map.setOnInfoWindowClickListener {
            home.gotoTripsList()
        }

        map.setOnMapClickListener {
            selectedStation= null
            home.onMarkerUnSelected()
            refreshMap()
        }
    }

    override fun checkReservation(){
        selectedStation= null
        home.onMarkerUnSelected()
        if(mode == MapMode.NEARBY) showNearby()
        else showAll()
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