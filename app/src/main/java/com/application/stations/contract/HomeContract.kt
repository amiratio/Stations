package com.application.stations.contract

interface HomeContract {

    interface View {

        fun showLoading()

        fun hideLoading()

    }

    interface Presenter {

        fun onAttach(view: View)

        fun onDetach()

        fun initialize()

    }

}