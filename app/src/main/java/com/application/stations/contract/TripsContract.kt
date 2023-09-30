package com.application.stations.contract

interface TripsContract {

    interface View {

        fun showLoading()

        fun hideLoading()

        fun showTripSelectionError()

        fun navigateBack()

    }

    interface Presenter {

        fun onTripSelected(tripId: Int)

        fun onBackButtonPressed()

    }

}