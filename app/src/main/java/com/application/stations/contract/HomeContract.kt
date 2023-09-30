package com.application.stations.contract

interface HomeContract {

    interface View {

        fun showLoading()

        fun hideLoading()

        fun onMarkerSelected()

        fun onMarkerUnSelected()

        fun gotoTripsList()

    }

    interface Presenter {

        fun onAttach(view: View)

        fun onDetach()

        fun initialize()

        fun gotoMyLocation()

        fun showNearbyStations()

        fun showAllStations()

        fun checkReservation()

    }

}