package com.application.stations.ui.activity

import android.os.Bundle
import com.application.stations.R
import com.application.stations.contract.TripsContract
import com.application.stations.core.BaseActivity
import com.application.stations.databinding.ActivityTripsBinding
import com.application.stations.presenter.TripsPresenter
import com.application.stations.remote.Repository
import com.application.stations.ui.adapter.TripsAdapter
import com.application.stations.utils.MapHelper.Companion.selectedStation
import com.application.stations.utils.Message
import com.application.stations.utils.extension.backgroundColorStatusBar
import com.application.stations.utils.extension.hide
import com.application.stations.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Trips : BaseActivity<ActivityTripsBinding>(), TripsContract.View {

    @Inject
    lateinit var repository: Repository

    private lateinit var presenter: TripsContract.Presenter

    override val binding by lazy { ActivityTripsBinding.inflate(layoutInflater) }
    private val station by lazy { selectedStation!! }

    companion object {
        var tripSelected = false
    }

    override fun onBackPressed() {
        presenter.onBackButtonPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundColorStatusBar()

        presenter = TripsPresenter(this, repository)

        uiSetup()
    }

    override fun showLoading() {
        binding.loading.root.show()
    }

    override fun hideLoading() {
        binding.loading.root.hide()
    }

    override fun showTripSelectionError() {
        Message(this).bookFailed()
    }

    override fun navigateBack() {
        finish()
        overridePendingTransition(R.anim.none, R.anim.activity_fade_out)
    }

    private fun uiSetup() {
        with(binding){
            loading.root.setOnClickListener { }
            back.setOnClickListener {
                onBackPressed()
            }
            tripSelected= false

            rv.adapter= TripsAdapter(station.trips?: arrayListOf()) {
                presenter.onTripSelected(it.id?: 0)
            }
        }
    }
}