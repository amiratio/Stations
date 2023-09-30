package com.application.stations.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.stations.databinding.ItemTripBinding
import com.application.stations.model.Trip
import com.application.stations.utils.GR
import com.application.stations.utils.extension.hide
import com.application.stations.utils.extension.show

class TripsAdapter(private val itemList: List<Trip>, private val gr: GR<Trip>) : RecyclerView.Adapter<TripsAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsAdapter.ViewHolder {
        context= parent.context
        val view= ItemTripBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripsAdapter.ViewHolder, position: Int) {
        val item= itemList[position]
        with(holder.binding){
            name.text= item.bus_name?:""
            time.text= item.time?:""
            if(position == itemList.size - 1) separator.hide()
            else separator.show()

            book.setOnClickListener {
                gr.result(itemList[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)
}