package com.application.stations.remote

import com.application.stations.model.Station
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService{

    @GET(RemoteConstants.STATIONS_LIST)
    suspend fun stations() : Response<List<Station>>

    @POST
    suspend fun bookTrip(
        @Url url: String,
    ) : Response<ResponseBody>

}