package com.application.stations.utils

import com.application.stations.utils.extension.displayHeight
import com.application.stations.utils.extension.displayWidth
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class MapHelper(private val map: GoogleMap) {

    fun limitToTurkey(){
        val one = LatLng(42.481692, 26.903169)
        val two = LatLng(36.272482, 44.635101)
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

}