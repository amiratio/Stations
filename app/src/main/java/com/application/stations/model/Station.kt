package com.application.stations.model

data class Station(
    val id: Int?,
    val name: String?,
    val trips_count: Int?,
    val center_coordinates: String?,
    val trips: List<Trip>?,
)