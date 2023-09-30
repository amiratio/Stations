package com.application.stations.utils.extension

import android.content.Context
import android.net.ConnectivityManager
import com.application.stations.R
import com.application.stations.model.ResponseResult
import com.application.stations.utils.Message
import retrofit2.Response

suspend fun <T> Context.apiCall(call: suspend () -> Response<T>) : ResponseResult<T> {

    return try {
        val apiResponse= call.invoke()
        ResponseResult(apiResponse.isSuccessful, apiResponse.body(), apiResponse.message())
    }catch (e: Exception){
        if(!hasInternet()){
            Message(this).noInternet()
        }
        ResponseResult(false, null)
    }

}

fun Context.hasInternet(): Boolean{
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    val cellular = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return wifi != null && wifi.isConnected || cellular != null && cellular.isConnected
}