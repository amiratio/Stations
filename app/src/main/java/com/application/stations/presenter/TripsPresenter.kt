package com.application.stations.presenter

import com.application.stations.contract.TripsContract
import com.application.stations.remote.Repository
import com.application.stations.ui.activity.Trips
import com.application.stations.utils.MapHelper
import com.application.stations.utils.extension.uiThread

class TripsPresenter (private var view: TripsContract.View, private val repository: Repository) :
    TripsContract.Presenter {

    private val station = MapHelper.selectedStation!!

    override fun onTripSelected(tripId: Int) {
        view.showLoading()
        repository.bookTrip(station.id.toString(), tripId.toString()) { isReserved ->
            uiThread {
                view.hideLoading()
                if (isReserved) {
                    for(s in MapHelper.stations){
                        if(s.id == station.id){
                            for(t in s.trips?: listOf()){
                                if(t.id == tripId) t.reserved= true
                            }
                            s.hasReserve= true
                            MapHelper.selectedStation = s
                        }
                    }
                    Trips.tripSelected = true
                    view.navigateBack()
                } else {
                    view.showTripSelectionError()
                }
            }
        }
    }

    override fun onBackButtonPressed() {
        view.navigateBack()
    }
}