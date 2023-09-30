package com.application.stations.utils

import com.application.stations.model.Station
import kotlin.math.cos
import kotlin.math.sin

class FilterNearbyStations(
    private val stations: ArrayList<Station>,
    private val currentLat: Double,
    private val currentLng: Double,
) {

    private val radius= 5 //Km

    fun filterWithinRadius(): ArrayList<Station> {
        val filteredStations = ArrayList<Station>()

        for (station in stations) {
            if(!station.center_coordinates.isNullOrBlank()){
                val lat= station.center_coordinates.split(",")[0].toDouble()
                val lng= station.center_coordinates.split(",")[1].toDouble()
                val distance = distanceBetween(currentLat, currentLng, lat, lng)
                if (distance <= radius) {
                    filteredStations.add(station)
                }
            }
        }

        return filteredStations
    }

    private fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c  // Distance in km
    }

}