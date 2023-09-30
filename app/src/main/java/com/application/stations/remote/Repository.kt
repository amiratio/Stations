package com.application.stations.remote

import android.app.Application
import com.application.stations.model.Station
import com.application.stations.utils.GR
import com.application.stations.utils.extension.apiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val application: Application,
)  {

    fun stations(gr: GR<List<Station>>) = CoroutineScope(Dispatchers.IO).launch {
        val response= application.apiCall {
            apiService.stations()
        }
        response.let {
            if(it.status){
                gr.result(it.data?: listOf())
            }
        }
    }

    fun bookTrip(stationId: String, tripId: String, gr: GR<Boolean>) = CoroutineScope(Dispatchers.IO).launch {
        val response= application.apiCall {
            apiService.bookTrip(
                RemoteConstants.BOOK_TRIP
                    .replace("STATION_ID", stationId)
                    .replace("TRIP_ID", tripId)
            )
        }
        response.let {
            gr.result(it.status)
        }
    }

}